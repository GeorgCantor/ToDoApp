package com.example.todoapp.presentation.visualization

import com.example.todoapp.domain.model.SpaceXLaunch

class YearChartVisualizer : Visualizer {
    override fun visualize(data: List<SpaceXLaunch>): VisualizationResult {
        val launchesByYear =
            data.groupingBy { it.launchYear.orEmpty() }.eachCount().toSortedMap()

        return VisualizationResult(
            title = getTitle(),
            type = VisualizerType.BAR_CHART,
            data = launchesByYear,
            metaData = mapOf("xLabel" to "Год", "yLabel" to "Количество запусков"),
        )
    }

    override fun getType() = VisualizerType.BAR_CHART

    override fun getTitle() = "Запуски по годам"
}
