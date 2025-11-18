package com.example.todoapp.domain.manager

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.example.todoapp.workers.SessionEndWorker

class SessionTracker(
    private val context: Context,
) : DefaultLifecycleObserver {
    private var sessionStartTime = 0L
    private var isSessionActive = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
    }

    override fun onStop(owner: LifecycleOwner) {
    }

    private fun onAppForegrounded() {
    }

    private fun onAppBackgrounded() {
    }

    private fun startNewSession() {
        sessionStartTime = System.currentTimeMillis()
        isSessionActive = true
    }

    private fun scheduleSessionEnd() {
        val sessionDuration = System.currentTimeMillis() - sessionStartTime

        val sessionData =
            workDataOf(
                "SESSION_DURATION" to sessionDuration,
                "SESSION_START_TIME" to sessionStartTime,
                "SESSION_END_TIME" to System.currentTimeMillis(),
            )

        val sessionEndWork = OneTimeWorkRequestBuilder<SessionEndWorker>()
    }
}
