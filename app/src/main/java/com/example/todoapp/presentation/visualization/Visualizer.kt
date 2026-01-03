package com.example.todoapp.presentation.visualization

import com.example.todoapp.domain.model.SpaceXLaunch

interface Visualizer {
    fun visualize(data: List<SpaceXLaunch>): VisualizationResult

    fun getType(): VisualizerType

    fun getTitle(): String
}

enum class VisualizerType {
    PIE_CHART,
    BAR_CHART,
    LINE_CHART,
    LIST,
    STATS,
}

data class VisualizationResult(
    val title: String,
    val type: VisualizerType,
    val data: Map<String, Any>,
    val metaData: Map<String, Any> = emptyMap(),
)
