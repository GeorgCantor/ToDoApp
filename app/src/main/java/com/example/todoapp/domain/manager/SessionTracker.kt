package com.example.todoapp.domain.manager

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.todoapp.workers.SessionEndWorker
import java.util.concurrent.TimeUnit

private const val SESSION_END = "SESSION_END"

class SessionTracker(
    private val context: Context,
) : DefaultLifecycleObserver {
    private var sessionStartTime = 0L
    private var isSessionActive = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        onAppForegrounded()
    }

    override fun onStop(owner: LifecycleOwner) {
        onAppBackgrounded()
    }

    private fun onAppForegrounded() {
        cancelPendingSessionEnd()
        if (!isSessionActive) startNewSession()
    }

    private fun onAppBackgrounded() {
        if (isSessionActive) scheduleSessionEnd()
    }

    private fun startNewSession() {
        sessionStartTime = System.currentTimeMillis()
        isSessionActive = true
        println("Session started at $sessionStartTime")
    }

    private fun scheduleSessionEnd() {
        val sessionDuration = System.currentTimeMillis() - sessionStartTime

        val sessionData =
            workDataOf(
                "SESSION_DURATION" to sessionDuration,
                "SESSION_START_TIME" to sessionStartTime,
                "SESSION_END_TIME" to System.currentTimeMillis(),
            )

        val sessionEndWork =
            OneTimeWorkRequestBuilder<SessionEndWorker>()
                .setInitialDelay(2, TimeUnit.SECONDS)
                .setInputData(sessionData)
                .addTag(SESSION_END)
                .build()

        WorkManager.getInstance(context).enqueue(sessionEndWork)
    }

    private fun cancelPendingSessionEnd() {
        WorkManager.getInstance(context).cancelAllWorkByTag(SESSION_END)
    }

    fun forceEndSession() {
        if (isSessionActive) {
            val sessionDuration = System.currentTimeMillis() - sessionStartTime
            logSessionEnd(sessionDuration)
        }
    }

    private fun logSessionEnd(duration: Long) {
        println("Session ended. Duration: $duration ms")
    }

    fun cleanup() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }
}
