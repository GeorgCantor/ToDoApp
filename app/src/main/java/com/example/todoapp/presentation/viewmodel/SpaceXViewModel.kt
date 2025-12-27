package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.SpaceXLaunch
import com.example.todoapp.domain.model.SpaceXUiState
import com.example.todoapp.domain.usecase.GetLaunchDetailUseCase
import com.example.todoapp.domain.usecase.GetSpaceXLaunchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceXViewModel(
    private val getLaunchesUseCase: GetSpaceXLaunchesUseCase,
    private val getDetailUseCase: GetLaunchDetailUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SpaceXUiState>(SpaceXUiState.Loading)
    val uiState: StateFlow<SpaceXUiState> = _uiState.asStateFlow()

    private val _selectedLaunch = MutableStateFlow<SpaceXLaunch?>(null)
    val selectedLaunch: StateFlow<SpaceXLaunch?> = _selectedLaunch.asStateFlow()

    private val _showDetail = MutableStateFlow(false)
    val showDetail: StateFlow<Boolean> = _showDetail.asStateFlow()

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading.asStateFlow()

    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError.asStateFlow()

    fun loadLaunches(limit: Int = 10) {
        viewModelScope.launch {
            _uiState.value = SpaceXUiState.Loading
            val result = getLaunchesUseCase(limit)
            _uiState.value =
                if (result.isSuccess) {
                    val launches = result.getOrThrow()
                    SpaceXUiState.Success(launches)
                } else {
                    SpaceXUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load data")
                }
        }
    }

    fun openLaunchDetail(launchId: String?) {
        launchId ?: return
        viewModelScope.launch {
            _showDetail.value = true
            _detailLoading.value = true
            _detailError.value = null
            val result = getDetailUseCase(launchId)
            if (result.isSuccess) {
                _selectedLaunch.value = result.getOrThrow()
                _detailError.value = null
            } else {
                _selectedLaunch.value = null
                _detailError.value = result.exceptionOrNull()?.message ?: "Failed to load details"
            }
            _detailLoading.value = false
        }
    }

    fun closeLaunchDetail() {
        _showDetail.value = false
        _selectedLaunch.value = null
        _detailError.value = null
        _detailLoading.value = false
    }
}
