package com.example.todoapp.widget

import android.content.Intent
import android.widget.RemoteViewsService

class ChatWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = ChatRemoteViewsFactory(this.applicationContext)
}
