package com.example.todoapp.domain.model

data class CoroutineInfo(
    val id: String,
    var name: String,
    var state: CoroutineState = CoroutineState.ACTIVE,
    val dispatcher: String = "Default",
    val startTime: Long = System.currentTimeMillis(),
    var children: Int = 0,
) {
    val duration: Long get() = System.currentTimeMillis() - startTime
}

enum class CoroutineState {
    ACTIVE,
    SUSPENDED,
    CANCELLING,
    CANCELLED,
    COMPLETED,
}

data class CoroutineMonitorData(
    val activeCoroutines: List<CoroutineInfo> = emptyList(),
    val totalCount: Int = 0,
    val byDispatcher: Map<String, Int> = emptyMap(),
    val byState: Map<CoroutineState, Int> = emptyMap(),
    val threads: List<ThreadInfo> = emptyList(),
)

data class ThreadInfo(
    val id: Long,
    val name: String,
    val state: Thread.State,
    val isDaemon: Boolean,
    val priority: Int,
)
