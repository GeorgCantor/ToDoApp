package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.NetworkMetrics
import kotlinx.coroutines.flow.Flow

interface NetworkMetricsRepository {
    suspend fun saveMetrics(metrics: NetworkMetrics)

    fun getAllMetrics(): Flow<List<NetworkMetrics>>

    suspend fun clearMetrics()
}
