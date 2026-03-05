package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.model.MapScreenState
import com.example.todoapp.domain.usecase.GetLocationHistoryUseCase
import com.example.todoapp.domain.usecase.GetLocationUseCase
import com.example.todoapp.domain.usecase.RequestExactLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val getLocationUseCase: GetLocationUseCase,
    private val requestExactLocationUseCase: RequestExactLocationUseCase,
    private val getLocationHistoryUseCase: GetLocationHistoryUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MapScreenState())
    val state: StateFlow<MapScreenState> = _state.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingHistory = true) }
            try {
                val points = getLocationHistoryUseCase()
                _state.update {
                    it.copy(
                        historyPoints = points,
                        isLoadingHistory = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingHistory = false,
                        error = "Failed to load history: ${e.message}",
                    )
                }
            }
        }
    }

    fun getCurrentLocation(forceExact: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true) }
            when (val result = getLocationUseCase(forceExact)) {
                is LocationResult.Success -> {
                    _state.update {
                        it.copy(
                            currentLocation = result,
                            isLoadingLocation = false,
                            error = null,
                        )
                    }
                    loadHistory()
                }

                is LocationResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            error = result.reason,
                        )
                    }
                }

                LocationResult.NotAvailable -> {
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            error = "Location not available",
                        )
                    }
                }
            }
        }
    }

    fun requestExactLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLocation = true) }
            when (val result = requestExactLocationUseCase()) {
                is LocationResult.Success -> {
                    _state.update {
                        it.copy(
                            currentLocation = result,
                            isLoadingLocation = false,
                            error = null,
                        )
                    }
                    loadHistory()
                }

                is LocationResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            error = result.reason,
                            showLocationSettingsDialog = true,
                        )
                    }
                }

                LocationResult.NotAvailable -> {
                    _state.update {
                        it.copy(
                            isLoadingLocation = false,
                            error = "Location not available",
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun dismissLocationSettingsDialog() {
        _state.update { it.copy(showLocationSettingsDialog = false) }
    }
}
