package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.CoroutineMonitorRepositoryImpl
import com.example.todoapp.domain.model.CoroutineMonitorData
import com.example.todoapp.domain.repository.CoroutineMonitorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoroutineMonitorViewModel(
    private val repository: CoroutineMonitorRepository,
) : ViewModel() {
    private val _monitorData = MutableStateFlow(CoroutineMonitorData())
    val monitorData: StateFlow<CoroutineMonitorData> = _monitorData.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private val _selectedCoroutineId = MutableStateFlow<String?>(null)
    val selectedCoroutineId: StateFlow<String?> = _selectedCoroutineId.asStateFlow()

    private val coroutineIds = mutableListOf<String>()

    init {
        startMonitoring()
        observeMonitorData()
    }

    fun startMonitoring() {
        viewModelScope.launch {
            repository.startMonitoring()
            _isMonitoring.value = true
        }
    }

    fun stopMonitoring() {
        viewModelScope.launch {
            repository.stopMonitoring()
            _isMonitoring.value = false
        }
    }

    fun toggleMonitoring() {
        if (_isMonitoring.value) stopMonitoring() else startMonitoring()
    }

    fun selectCoroutine(id: String?) {
        _selectedCoroutineId.value = id
    }

    fun createCoroutines() {
        clearCoroutines()
        viewModelScope.launch {
            val id = (repository as CoroutineMonitorRepositoryImpl).createCoroutine("Long task", "Default")
            coroutineIds.add(id)
        }
    }

    private fun clearCoroutines() {
        coroutineIds.clear()
    }

    private fun observeMonitorData() {
        viewModelScope.launch {
            repository.getMonitorData().collectLatest { _monitorData.value = it }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
