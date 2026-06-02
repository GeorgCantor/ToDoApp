package com.example.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.example.todoapp.domain.model.NetworkMetrics
import com.example.todoapp.domain.model.NetworkMetricsList
import com.example.todoapp.domain.repository.NetworkMetricsRepository
import kotlinx.coroutines.flow.map

class NetworkMetricsRepositoryImpl(
    private val dataStore: DataStore<NetworkMetricsList>,
) : NetworkMetricsRepository {
    override suspend fun saveMetrics(metrics: NetworkMetrics) {
        dataStore.updateData {
            NetworkMetricsList(listOf(metrics) + it.metrics)
        }
    }

    override fun getAllMetrics() = dataStore.data.map { it.metrics }

    override suspend fun clearMetrics() {
        dataStore.updateData { NetworkMetricsList() }
    }
}
