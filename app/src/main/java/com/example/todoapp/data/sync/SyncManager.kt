package com.example.todoapp.data.sync

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.example.todoapp.data.provider.MessageContentProvider
import com.example.todoapp.domain.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SyncState { IDLE, SYNCING }

class SyncManager(
    private val context: Context,
    private val p2PManager: P2PManager,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    private val syncState = _syncState.asStateFlow()

    val uiState =
        combine(_syncState, p2PManager.connectionStatus) { sync, connection ->
            when {
                connection == ConnectionStatus.CONNECTED && sync == SyncState.SYNCING -> "Синхронизация"
                connection == ConnectionStatus.CONNECTED -> "Подключено"
                connection == ConnectionStatus.ADVERTISING -> "Ожидание подключения"
                connection == ConnectionStatus.DISCOVERING -> "Поиск устройств"
                else -> "Не подключено"
            }
        }.stateIn(scope, SharingStarted.Eagerly, "Не подключено")

    init {
        scope.launch {
            p2PManager.incomingMessages.collect {
                saveReceivedMessage(it)
            }
        }
    }

    private val contentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(
                selfChange: Boolean,
                uri: Uri?,
            ) {
                super.onChange(selfChange, uri)
                checkAndSync()
            }
        }

    fun startAdvertising(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        p2PManager.startAdvertising(onSuccess, onFailure)
    }

    fun startDiscovery(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        p2PManager.startDiscovery(onSuccess, onFailure)
    }

    fun stopP2P() = p2PManager.stop()

    fun start() {
        context.contentResolver.registerContentObserver(
            MessageContentProvider.contentUri,
            true,
            contentObserver,
        )
        checkAndSync()
    }

    fun stop() {
        context.contentResolver.unregisterContentObserver(contentObserver)
        scope.cancel()
    }

    private suspend fun saveReceivedMessage(message: Message) =
        withContext(Dispatchers.IO) {
            if (message.text == "SYNC_REQUEST") return@withContext
            try {
                val values =
                    ContentValues().apply {
                        put(MessageContentProvider.COLUMN_ID, message.id)
                        put(MessageContentProvider.COLUMN_TEXT, message.text)
                        put(MessageContentProvider.COLUMN_SENDER, message.sender)
                        put(MessageContentProvider.COLUMN_TIMESTAMP, message.timestamp)
                        put(MessageContentProvider.COLUMN_SYNCED, 1)
                    }
                if (getMessageById(message.id) == null) {
                    context.contentResolver.insert(MessageContentProvider.contentUri, values)
                } else {
                    val updateUri = Uri.withAppendedPath(MessageContentProvider.contentUri, message.id)
                    context.contentResolver.update(updateUri, values, null, null)
                }
            } catch (e: Exception) {
            }
        }

    private suspend fun getMessageById(messageId: String): Message? =
        withContext(Dispatchers.IO) {
            val uri = Uri.withAppendedPath(MessageContentProvider.contentUri, messageId)
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idCol = it.getColumnIndex(MessageContentProvider.COLUMN_ID)
                    val textCol = it.getColumnIndex(MessageContentProvider.COLUMN_TEXT)
                    val senderCol = it.getColumnIndex(MessageContentProvider.COLUMN_SENDER)
                    val timestampCol = it.getColumnIndex(MessageContentProvider.COLUMN_TIMESTAMP)
                    val syncedCol = it.getColumnIndex(MessageContentProvider.COLUMN_SYNCED)
                    return@withContext Message(
                        id = it.getString(idCol),
                        text = it.getString(textCol),
                        sender = it.getString(senderCol),
                        timestamp = it.getLong(timestampCol),
                        synced = it.getInt(syncedCol) == 1,
                    )
                }
            }
            null
        }

    private fun checkAndSync() {
        scope.launch {
            val unsyncedMessages = getUnsyncedMessages()
            if (unsyncedMessages.isNotEmpty() && _syncState.value == SyncState.IDLE) {
                _syncState.value = SyncState.SYNCING
                try {
                    syncMessages(unsyncedMessages)
                } finally {
                    _syncState.value = SyncState.IDLE
                }
            }
        }
    }

    private suspend fun getUnsyncedMessages(): List<Message> =
        withContext(Dispatchers.IO) {
            val cursor =
                context.contentResolver.query(
                    MessageContentProvider.contentUri,
                    null,
                    null,
                    null,
                    "${MessageContentProvider.COLUMN_TIMESTAMP} ASC",
                )
            buildList {
                cursor?.use {
                    val idCol = it.getColumnIndex(MessageContentProvider.COLUMN_ID)
                    val textCol = it.getColumnIndex(MessageContentProvider.COLUMN_TEXT)
                    val senderCol = it.getColumnIndex(MessageContentProvider.COLUMN_SENDER)
                    val timestampCol = it.getColumnIndex(MessageContentProvider.COLUMN_TIMESTAMP)
                    val syncedCol = it.getColumnIndex(MessageContentProvider.COLUMN_SYNCED)
                    while (it.moveToNext()) {
                        if (it.getInt(syncedCol) != 1) {
                            add(
                                Message(
                                    id = it.getString(idCol),
                                    text = it.getString(textCol),
                                    sender = it.getString(senderCol),
                                    timestamp = it.getLong(timestampCol),
                                    synced = false,
                                ),
                            )
                        }
                    }
                }
            }
        }

    private suspend fun syncMessages(messages: List<Message>) {
        messages.forEach {
            try {
                if (p2PManager.sendMessage(it)) markAsSynced(it.id)
            } catch (e: Exception) {
            }
        }
        if (getUnsyncedMessages().isNotEmpty()) checkAndSync()
    }

    private fun markAsSynced(messageId: String) {
        val values = ContentValues().apply { put(MessageContentProvider.COLUMN_SYNCED, 1) }
        val updateUri = Uri.withAppendedPath(MessageContentProvider.contentUri, messageId)
        context.contentResolver.update(updateUri, values, null, null)
    }
}
