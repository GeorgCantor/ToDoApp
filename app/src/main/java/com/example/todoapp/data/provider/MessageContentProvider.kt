package com.example.todoapp.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.core.net.toUri
import com.example.todoapp.BuildConfig
import com.example.todoapp.data.datasource.MessageDataSource
import com.example.todoapp.data.datasource.MessageDataSourceImpl
import com.example.todoapp.domain.model.Message
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get

class MessageContentProvider : ContentProvider() {
    private lateinit var dataSource: MessageDataSource

    companion object {
        private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.messages"
        val contentUri = "content://$AUTHORITY/messages".toUri()
        private const val MESSAGES = 1
        private const val MESSAGE_ID = 2

        private val uriMatcher =
            UriMatcher(UriMatcher.NO_MATCH).apply {
                addURI(AUTHORITY, "messages", MESSAGES)
                addURI(AUTHORITY, "messages/#", MESSAGE_ID)
            }

        const val COLUMN_ID = "_id"
        const val COLUMN_TEXT = "text"
        const val COLUMN_SENDER = "sender"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_SYNCED = "synced"
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        val messageId = uri.lastPathSegment ?: return 0
        val existingMessages = runBlocking { dataSource.getAllMessages() }
        val updatedMessages = existingMessages.filter { it.id != messageId }
        if (existingMessages.size == updatedMessages.size) return 0
        runBlocking { dataSource.saveMessages(updatedMessages) }
        context?.contentResolver?.notifyChange(uri, null)
        return 1
    }

    override fun getType(uri: Uri) =
        when (uriMatcher.match(uri)) {
            MESSAGES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.messages"
            MESSAGE_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.messages"
            else -> null
        }

    override fun insert(
        uri: Uri,
        values: ContentValues?,
    ): Uri? {
        values ?: return null
        val id = values.getAsString(COLUMN_ID) ?: return null
        val text = values.getAsString(COLUMN_TEXT) ?: return null
        val sender = values.getAsString(COLUMN_SENDER) ?: return null
        val timestamp = values.getAsLong(COLUMN_TIMESTAMP)
        val synced = values.getAsInteger(COLUMN_SYNCED) == 1
        runBlocking {
            dataSource.saveMessage(Message(id, text, sender, timestamp, synced))
        }
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(contentUri, id)
    }

    override fun onCreate(): Boolean {
        context?.let {
            val gson: Gson = get(Gson::class.java)
            dataSource = MessageDataSourceImpl(it.applicationContext, gson)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        val messages = runBlocking { dataSource.getAllMessages() }
        val cursor = MatrixCursor(arrayOf(COLUMN_ID, COLUMN_TEXT, COLUMN_SENDER, COLUMN_TIMESTAMP, COLUMN_SYNCED))
        messages.forEach {
            cursor.addRow(arrayOf(it.id, it.text, it.sender, it.timestamp, if (it.synced) 1 else 0))
        }
        context?.contentResolver?.notifyChange(uri, null)
        return cursor
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        values ?: return 0
        val messageId = uri.lastPathSegment ?: return 0
        val text = values.getAsString(COLUMN_TEXT)
        val sender = values.getAsString(COLUMN_SENDER)
        val timestamp = values.getAsLong(COLUMN_TIMESTAMP)
        val synced = values.getAsInteger(COLUMN_SYNCED) == 1

        val messages = runBlocking { dataSource.getAllMessages() }
        messages.find { it.id == messageId }?.let {
            val updated =
                it.copy(
                    text = text ?: it.text,
                    sender = sender ?: it.sender,
                    timestamp = if (timestamp != 0L) timestamp else it.timestamp,
                    synced = synced,
                )
            runBlocking { dataSource.saveMessage(updated) }
            context?.contentResolver?.notifyChange(uri, null)
            return 1
        } ?: return 0
    }
}
