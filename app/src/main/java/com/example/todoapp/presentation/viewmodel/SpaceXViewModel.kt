package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.SpaceXUiState
import com.example.todoapp.domain.usecase.GetSpaceXLaunchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceXViewModel(
    private val useCase: GetSpaceXLaunchesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SpaceXUiState>(SpaceXUiState.Loading)
    val uiState: StateFlow<SpaceXUiState> = _uiState.asStateFlow()

    fun loadLaunches(limit: Int = 10) {
        viewModelScope.launch {
            _uiState.value = SpaceXUiState.Loading
            val result = useCase(limit)
            _uiState.value =
                if (result.isSuccess) {
                    val launches = result.getOrThrow()
                    SpaceXUiState.Success(launches)
                } else {
                    SpaceXUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load data")
                }
        }
    }
}
