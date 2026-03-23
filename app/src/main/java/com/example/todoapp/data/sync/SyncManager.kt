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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SyncState { IDLE, SYNCING }

class SyncManager(
    private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    private val syncState = _syncState.asStateFlow()

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

    private fun checkAndSync() {
        scope.launch {
            val unsyncedMessages = getUnsyncedMessages()
            if (unsyncedMessages.isNotEmpty() && _syncState.value == SyncState.IDLE) {
                _syncState.value = SyncState.SYNCING
                syncMessages(unsyncedMessages)
                _syncState.value = SyncState.IDLE
            }
        }
    }

    private suspend fun getUnsyncedMessages(): List<Message> =
        withContext(Dispatchers.IO) {
            val unsyncedUri = Uri.withAppendedPath(MessageContentProvider.contentUri, "unsynced")
            val cursor = context.contentResolver.query(unsyncedUri, null, null, null, null)
            buildList {
                cursor?.use {
                    val idCol = it.getColumnIndex(MessageContentProvider.COLUMN_ID)
                    val textCol = it.getColumnIndex(MessageContentProvider.COLUMN_TEXT)
                    val senderCol = it.getColumnIndex(MessageContentProvider.COLUMN_SENDER)
                    val timestampCol = it.getColumnIndex(MessageContentProvider.COLUMN_TIMESTAMP)
                    val syncedCol = it.getColumnIndex(MessageContentProvider.COLUMN_SYNCED)
                    while (it.moveToNext()) {
                        add(
                            Message(
                                id = it.getString(idCol),
                                text = it.getString(textCol),
                                sender = it.getString(senderCol),
                                timestamp = it.getLong(timestampCol),
                                synced = it.getInt(syncedCol) == 1,
                            ),
                        )
                    }
                }
            }
        }

    private suspend fun syncMessages(messages: List<Message>) {
        // TODO: Пока иммитация
        messages.forEach {
            delay(1000)
            markAsSynced(it.id)
        }
    }

    private fun markAsSynced(messageId: String) {
        val values = ContentValues().apply { put(MessageContentProvider.COLUMN_SYNCED, messageId) }
        val updateUri = Uri.withAppendedPath(MessageContentProvider.contentUri, messageId)
        context.contentResolver.update(updateUri, values, null, null)
    }
}
