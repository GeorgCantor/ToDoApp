package com.example.todoapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMetricsList(
    val metrics: List<NetworkMetrics> = emptyList(),
)
