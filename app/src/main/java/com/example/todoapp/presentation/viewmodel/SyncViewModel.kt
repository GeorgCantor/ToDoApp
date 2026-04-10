package com.example.todoapp.presentation.viewmodel

import android.content.ContentResolver
import android.content.ContentValues
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.provider.MessageContentProvider
import com.example.todoapp.data.sync.SyncManager
import com.example.todoapp.domain.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class SyncViewModel(
    private val syncManager: SyncManager,
    private val contentResolver: ContentResolver,
) : ViewModel() {
    val connectionStatus = syncManager.uiState

    val messages =
        flow {
            while (true) {
                val cursor =
                    contentResolver.query(
                        MessageContentProvider.contentUri,
                        null,
                        null,
                        null,
                        "${MessageContentProvider.COLUMN_TIMESTAMP} ASC",
                    )
                val messages =
                    cursor
                        ?.use {
                            buildList {
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
                        }.orEmpty()
                emit(messages)
                delay(500)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun sendMessage(text: String) {
        val message =
            Message(
                id = UUID.randomUUID().toString(),
                text = text,
                sender = android.os.Build.MODEL,
                timestamp = System.currentTimeMillis(),
                synced = false,
            )

        val values =
            ContentValues().apply {
                put(MessageContentProvider.COLUMN_ID, message.id)
                put(MessageContentProvider.COLUMN_TEXT, message.text)
                put(MessageContentProvider.COLUMN_SENDER, message.sender)
                put(MessageContentProvider.COLUMN_TIMESTAMP, message.timestamp)
                put(MessageContentProvider.COLUMN_SYNCED, 0)
            }

        contentResolver.insert(MessageContentProvider.contentUri, values)
    }

    fun startAdvertising() {
        syncManager.startAdvertising({}, {})
    }

    fun startDiscovery() {
        syncManager.startDiscovery({}, {})
    }

    fun stopP2P() = syncManager.stopP2P()

    override fun onCleared() {
        syncManager.stop()
        super.onCleared()
    }
}
