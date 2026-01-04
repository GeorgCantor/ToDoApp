package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.CoroutineInfo
import com.example.todoapp.domain.model.CoroutineMonitorData
import com.example.todoapp.domain.repository.CoroutineMonitorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex

class CoroutineMonitorRepositoryImpl : CoroutineMonitorRepository {
    private val _monitorData = MutableStateFlow(CoroutineMonitorData())

    override fun getMonitorData(): StateFlow<CoroutineMonitorData> = _monitorData.asStateFlow()

    private val monitorJob: Job? = null
    private val mutex = Mutex()
    private val monitoringScope = CoroutineScope(Dispatchers.Default)

    override suspend fun captureCoroutines(): List<CoroutineInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun startMonitoring() {
        TODO("Not yet implemented")
    }

    override suspend fun stopMonitoring() {
        TODO("Not yet implemented")
    }

    override fun isMonitoring(): Boolean {
        TODO("Not yet implemented")
    }
}
