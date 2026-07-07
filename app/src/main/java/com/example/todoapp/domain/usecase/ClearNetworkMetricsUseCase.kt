package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.NetworkMetricsRepository

class ClearNetworkMetricsUseCase(
    private val repository: NetworkMetricsRepository,
) {
    suspend operator fun invoke() = repository.clearMetrics()
}
