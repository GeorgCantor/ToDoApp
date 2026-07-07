package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.NetworkMetrics
import com.example.todoapp.domain.usecase.ClearNetworkMetricsUseCase
import com.example.todoapp.domain.usecase.GetNetworkMetricsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkStatsViewModel(
    private val getMetricsUseCase: GetNetworkMetricsUseCase,
    private val clearMetricsUseCase: ClearNetworkMetricsUseCase,
) : ViewModel() {
    private val _metrics = MutableStateFlow<List<NetworkMetrics>>(emptyList())
    val metrics = _metrics.asStateFlow()

    init {
        viewModelScope.launch {
            getMetricsUseCase().collect { _metrics.value = it }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { clearMetricsUseCase() }
    }

    fun getAverageTotalTime(): Long {
        val list = _metrics.value
        if (list.isEmpty()) return 0
        return list.map { it.totalDurationMs }.average().toLong()
    }

    fun getSuccessRate(): Float {
        val list = _metrics.value
        if (list.isEmpty()) return 0F
        return list.count { it.success }.toFloat() / list.size
    }

    fun getErrorCount() = _metrics.value.count { !it.success }
}
