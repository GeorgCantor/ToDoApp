package com.example.todoapp.presentation.viewmodel

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.provider.MessageContentProvider
import com.example.todoapp.data.sync.SyncManager
import com.example.todoapp.domain.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

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

    fun startAdvertising() {
        syncManager.startAdvertising({}, {})
    }

    fun startDiscovery() {
        syncManager.startDiscovery({}, {})
    }

    fun stopP2P() = syncManager.stopP2P()

    override fun onCleared() {
        syncManager.stopP2P()
        super.onCleared()
    }
}
