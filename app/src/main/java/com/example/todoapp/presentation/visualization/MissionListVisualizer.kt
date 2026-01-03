package com.example.todoapp.presentation.visualization

import com.example.todoapp.domain.model.SpaceXLaunch

class MissionListVisualizer : Visualizer {
    override fun visualize(data: List<SpaceXLaunch>): VisualizationResult {
        val latestMissions =
            data
                .sortedByDescending { it.launchDateUtc }
                .take(10)
                .associate {
                    it.missionName.orEmpty() to
                        mapOf(
                            "date" to it.launchDateUtc,
                            "success" to it.launchSuccess,
                            "rocket" to it.rocketName,
                        )
                }

        return VisualizationResult(
            title = getTitle(),
            type = VisualizerType.LIST,
            data = latestMissions,
            metaData = mapOf("itemsCount" to latestMissions.size),
        )
    }

    override fun getType() = VisualizerType.LIST

    override fun getTitle() = "Последние 10 миссий"
}
