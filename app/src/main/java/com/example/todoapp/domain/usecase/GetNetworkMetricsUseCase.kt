package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.NetworkMetricsRepository

class GetNetworkMetricsUseCase(
    private val repository: NetworkMetricsRepository,
) {
    operator fun invoke() = repository.getAllMetrics()
}
