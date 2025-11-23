package com.example.todoapp.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class SessionEndWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val repository: UserProfileRepository by inject(UserProfileRepository::class.java)

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            try {
                val sessionDuration = inputData.getLong("SESSION_DURATION", 0)

                repository.updateStatistics { currentStats ->
                    currentStats.copy(
                        totalSessionTime = currentStats.totalSessionTime + sessionDuration,
                        sessionsCount = currentStats.sessionsCount + 1,
                        lastSessionDuration = sessionDuration,
                    )
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
}
