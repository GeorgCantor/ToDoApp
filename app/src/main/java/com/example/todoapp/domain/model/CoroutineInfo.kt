package com.example.todoapp.domain.model

import java.util.UUID

data class CoroutineInfo(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val dispatcher: String,
    val state: CoroutineState,
    val startTime: Long = System.currentTimeMillis(),
    val stackTrace: List<String> = emptyList(),
    val parentId: String? = null,
    val childrenCount: Int = 0,
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
