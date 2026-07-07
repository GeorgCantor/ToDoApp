package com.example.todoapp.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkMetrics(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val method: String,
    val startTimeMs: Long,
    val dnsDurationMs: Long? = null,
    val connectDurationMs: Long? = null,
    val tlsDurationMs: Long? = null,
    val requestHeadersDurationMs: Long? = null,
    val requestBodyDurationMs: Long? = null,
    val responseHeadersDurationMs: Long? = null,
    val responseBodyDurationMs: Long? = null,
    val totalDurationMs: Long,
    val responseCode: Int? = null,
    val errorMessage: String? = null,
    val success: Boolean,
)
