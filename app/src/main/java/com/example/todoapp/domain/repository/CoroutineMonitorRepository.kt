package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.CoroutineInfo
import com.example.todoapp.domain.model.CoroutineMonitorData
import kotlinx.coroutines.flow.Flow

interface CoroutineMonitorRepository {
    fun getMonitorData(): Flow<CoroutineMonitorData>

    suspend fun captureCoroutines(): List<CoroutineInfo>

    suspend fun startMonitoring()

    suspend fun stopMonitoring()

    fun isMonitoring(): Boolean
}
