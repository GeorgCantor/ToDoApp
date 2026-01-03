package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.SpaceXLaunch
import com.example.todoapp.domain.model.StatsUiState
import com.example.todoapp.domain.usecase.GetSpaceXLaunchesUseCase
import com.example.todoapp.presentation.visualization.VisualizationResult
import com.example.todoapp.presentation.visualization.VisualizerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceXStatsViewModel(
    private val useCase: GetSpaceXLaunchesUseCase,
    private val factory: VisualizerFactory,
) : ViewModel() {
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val _visualizations = MutableStateFlow<List<VisualizationResult>>(emptyList())
    val visualizations: StateFlow<List<VisualizationResult>> = _visualizations.asStateFlow()

    private var allLaunches: List<SpaceXLaunch> = emptyList()

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            val result = useCase(50)
            if (result.isSuccess) {
                allLaunches = result.getOrThrow()
                generateVisualizations()
                _uiState.value = StatsUiState.Success
            } else {
                _uiState.value =
                    StatsUiState.Error(
                        result.exceptionOrNull()?.message ?: "Ошибка загрузки данных",
                    )
            }
        }
    }

    private fun generateVisualizations() {
        val results = mutableListOf<VisualizationResult>()

        val successVisualizer = factory.createSuccessChart()
        results.add(successVisualizer.visualize(allLaunches))

        val yearVisualizer = factory.createYearChart()
        results.add(yearVisualizer.visualize(allLaunches))

        val rocketVisualizer = factory.createRocketChart()
        results.add(rocketVisualizer.visualize(allLaunches))

        val missionVisualizer = factory.createMissionList()
        results.add(missionVisualizer.visualize(allLaunches))

        _visualizations.value = results
    }
}
