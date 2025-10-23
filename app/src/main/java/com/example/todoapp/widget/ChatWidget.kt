package com.example.todoapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.todoapp.R
import com.example.todoapp.presentation.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
    }

    override fun onEnabled(context: Context) {
        setupFirebaseListener(context)
    }

    private fun setupFirebaseListener(context: Context) {
        val database =
            com.google.firebase.database.FirebaseDatabase
                .getInstance()
                .getReference("chat_messages")

        database.limitToLast(10).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val appWidgetIds =
                        appWidgetManager.getAppWidgetIds(
                            ComponentName(context, ChatWidget::class.java),
                        )
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
                }

                override fun onCancelled(error: DatabaseError) {}
            },
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val views = RemoteViews(context.packageName, R.layout.chat_widget)

        val intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra("open_chat", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        val adapterIntent =
            Intent(context, ChatWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_ALLOW_UNSAFE).toUri()
            }
        views.setRemoteAdapter(R.id.widget_list, adapterIntent)
        views.setEmptyView(R.id.widget_list, R.id.widget_empty_text)

        val newMessageIntent =
            Intent(context, MainActivity::class.java).apply {
                action = "NEW_MESSAGE"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        val newMessagePendingIntent =
            PendingIntent.getActivity(
                context,
                1,
                newMessageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        views.setOnClickPendingIntent(R.id.widget_send_button, newMessagePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
    }
}
