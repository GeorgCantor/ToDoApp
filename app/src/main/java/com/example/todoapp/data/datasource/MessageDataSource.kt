package com.example.todoapp.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapp.domain.model.Message
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.messageDatastore: DataStore<Preferences> by preferencesDataStore("messages")

interface MessageDataSource {
    suspend fun getAllMessages(): List<Message>

    suspend fun saveMessage(message: Message)

    suspend fun saveMessages(messages: List<Message>)

    suspend fun clearAllMessages()
}

class MessageDataSourceImpl(
    private val context: Context,
) : MessageDataSource {
    private val messageKey = stringPreferencesKey("messages_list")

    override suspend fun getAllMessages(): List<Message> {
        val json = context.messageDatastore.data.first()[messageKey]
        return if (json.isNullOrBlank()) {
            emptyList()
        } else {
            Json.decodeFromString(json)
        }
    }

    override suspend fun saveMessage(message: Message) {
        val messages = getAllMessages().toMutableList()
        val index = messages.indexOfFirst { it.id == message.id }
        if (index >= 0) {
            messages[index] = message
        } else {
            messages.add(message)
        }
        saveMessages(messages)
    }

    override suspend fun saveMessages(messages: List<Message>) {
        val json = Json.encodeToString(messages)
        context.messageDatastore.edit { prefs ->
            prefs[messageKey] = json
        }
    }

    override suspend fun clearAllMessages() {
        context.messageDatastore.edit { it.remove(messageKey) }
    }
}
