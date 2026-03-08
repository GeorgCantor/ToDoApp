package com.example.todoapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.usecase.GetLocationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationWorker(
    context: Context,
    params: WorkerParameters,
    private val getLocationUseCase: GetLocationUseCase,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            return@withContext try {
                when (val location = getLocationUseCase()) {
                    is LocationResult.Success -> {
                        Result.success(workDataOf("source" to location.source.name))
                    }
                    is LocationResult.NotAvailable -> Result.retry()
                    is LocationResult.Error -> {
                        if (location.reason.contains("permission", true)) {
                            Result.failure(workDataOf("error" to location.reason))
                        } else {
                            Result.retry()
                        }
                    }
                }
            } catch (e: Exception) {
                Result.retry()
            }
        }
}
