package com.example.todoapp.presentation.visualization

interface VisualizerFactory {
    fun createSuccessChart(): Visualizer

    fun createYearChart(): Visualizer

    fun createRocketChart(): Visualizer

    fun createMissionList(): Visualizer
}

class SpaceXVisualizerFactory : VisualizerFactory {
    override fun createSuccessChart(): Visualizer = SuccessChartVisualizer()

    override fun createYearChart(): Visualizer = YearChartVisualizer()

    override fun createRocketChart(): Visualizer = RocketChartVisualizer()

    override fun createMissionList(): Visualizer = MissionListVisualizer()
}
