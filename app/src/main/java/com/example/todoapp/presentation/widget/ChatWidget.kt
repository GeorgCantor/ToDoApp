package com.example.todoapp.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.todoapp.R
import com.example.todoapp.presentation.MainActivity

class ChatWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { updateAppWidget(context, appWidgetManager, it) }
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
    }
}
