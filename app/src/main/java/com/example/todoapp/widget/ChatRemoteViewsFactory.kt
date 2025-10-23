package com.example.todoapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.todoapp.R
import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.presentation.MainActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRemoteViewsFactory(
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {
    private val messages = mutableListOf<ChatMessage>()
    private val database = FirebaseDatabase.getInstance().getReference("chat_messages")

    override fun getCount(): Int = messages.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewAt(position: Int): RemoteViews {
        val message = messages.getOrNull(position)
        val views = RemoteViews(context.packageName, R.layout.widget_message_item)

        views.setTextViewText(R.id.widget_message_sender, message?.senderName)
        views.setTextViewText(R.id.widget_message_text, getMessagePreview(message))
        views.setTextViewText(R.id.widget_message_time, formatTime(message?.timestamp ?: 0L))

        if (message?.audioBase64 != null) {
            views.setTextViewText(R.id.widget_message_text, "🎤 Аудиосообщение")
            views.setInt(R.id.widget_message_text, "setBackgroundResource", R.drawable.widget_audio_bg)
        } else {
            views.setInt(R.id.widget_message_text, "setBackgroundResource", R.drawable.widget_text_bg)
        }

        val intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra("open_chat", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        views.setOnClickFillInIntent(R.id.widget_message_item, intent)

        return views
    }

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun onCreate() {
        loadMessages()
    }

    override fun onDataSetChanged() {
        loadMessages()
    }

    override fun onDestroy() {
    }

    private fun loadMessages() =
        runBlocking {
            try {
                val snapshot = database.limitToLast(5).get().await()
                val newMessages =
                    snapshot.children
                        .mapNotNull { child ->
                            child.getValue(ChatMessage::class.java)?.copy(id = child.key.orEmpty())
                        }.sortedByDescending { it.timestamp }
                        .take(5)
                        .reversed()
                messages.clear()
                messages.addAll(newMessages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun getMessagePreview(message: ChatMessage?): String =
        if ((message?.text?.length ?: 0) > 30) {
            "${message?.text?.take(30)}..."
        } else {
            message?.text.orEmpty()
        }

    private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
