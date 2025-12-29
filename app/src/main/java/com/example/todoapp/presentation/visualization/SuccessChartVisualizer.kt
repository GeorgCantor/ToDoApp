package com.example.todoapp.presentation.visualization

import com.example.todoapp.domain.model.SpaceXLaunch

class SuccessChartVisualizer : Visualizer {
    override fun visualize(data: List<SpaceXLaunch>): VisualizationResult {
        val successful = data.count { it.launchSuccess == true }
        val failed = data.count { it.launchSuccess == false }
        val upcoming = data.count { it.launchSuccess == null }

        return VisualizationResult(
            title = getTitle(),
            type = getType(),
            data =
                mapOf(
                    "successful" to successful,
                    "failed" to failed,
                    "upcoming" to upcoming,
                ),
            metaData =
                mapOf(
                    "colors" to listOf("#4CAF50", "#F44336", "#2196F3"),
                    "labels" to listOf("Успешные", "Неудачные", "Предстоящие"),
                ),
        )
    }

    override fun getType() = VisualizerType.PIE_CHART

    override fun getTitle() = " Статистика успешности запусков"
}
