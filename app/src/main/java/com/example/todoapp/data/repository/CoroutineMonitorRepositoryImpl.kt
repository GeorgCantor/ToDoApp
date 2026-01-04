package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.CoroutineInfo
import com.example.todoapp.domain.model.CoroutineMonitorData
import com.example.todoapp.domain.model.ThreadInfo
import com.example.todoapp.domain.repository.CoroutineMonitorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CoroutineMonitorRepositoryImpl : CoroutineMonitorRepository {
    private val _monitorData = MutableStateFlow(CoroutineMonitorData())

    override fun getMonitorData(): StateFlow<CoroutineMonitorData> = _monitorData.asStateFlow()

    private var monitorJob: Job? = null
    private val monitoringScope = CoroutineScope(Dispatchers.Default)
    private val coroutines = mutableMapOf<String, CoroutineInfo>()

    override suspend fun captureCoroutines(): List<CoroutineInfo> = coroutines.map { it.value }

    override suspend fun startMonitoring() {
        stopMonitoring()
        monitorJob =
            monitoringScope.launch {
                while (isActive) {
                    updateMonitorData()
                    delay(1000)
                }
            }
    }

    override suspend fun stopMonitoring() {
        TODO("Not yet implemented")
    }

    override fun isMonitoring(): Boolean {
        TODO("Not yet implemented")
    }

    private suspend fun updateMonitorData() {
        val coroutines = captureCoroutines()
        val threads = getSimpleThreadsInfo()
        val byDispatcher = coroutines.groupingBy { it.dispatcher }.eachCount()
        val byState = coroutines.groupingBy { it.state }.eachCount()

        _monitorData.value =
            CoroutineMonitorData(
                activeCoroutines = coroutines,
                totalCount = coroutines.size,
                byDispatcher = byDispatcher,
                byState = byState,
                threads = threads,
            )
    }

    private fun getSimpleThreadsInfo(): List<ThreadInfo> {
        val threadCount = Thread.activeCount()
        val threads = arrayOfNulls<Thread>(threadCount * 2)
        val actualCount = Thread.enumerate(threads)

        return threads.take(actualCount).mapNotNull {
            ThreadInfo(
                id = it?.id ?: 0L,
                name = it?.name.orEmpty(),
                state = it?.state ?: Thread.State.TERMINATED,
                isDaemon = it?.isDaemon ?: false,
                priority = it?.priority ?: 0,
            )
        }
    }
}
