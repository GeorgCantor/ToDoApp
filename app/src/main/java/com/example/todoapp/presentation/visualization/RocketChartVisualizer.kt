package com.example.todoapp.presentation.visualization

import com.example.todoapp.domain.model.SpaceXLaunch

class RocketChartVisualizer : Visualizer {
    override fun visualize(data: List<SpaceXLaunch>): VisualizationResult {
        val launchesByRocket = data.groupingBy { it.rocketName }.eachCount()

        return VisualizationResult(
            title = getTitle(),
            type = VisualizerType.BAR_CHART,
            data = launchesByRocket,
            metaData = mapOf("xLabel" to "Ракета", "yLabel" to "Запуски"),
        )
    }

    override fun getType() = VisualizerType.BAR_CHART

    override fun getTitle() = "Распределение по ракетам"
}
