package com.example.todoapp.domain.model

data class LaunchStatistics(
    val totalLaunches: Int,
    val successfulLaunches: Int,
    val failedLaunches: Int,
    var successRate: Double,
    val launchesByYear: Map<String, Int>,
    val launchesByRocket: Map<String, Int>,
) {
    init {
        successRate =
            if (totalLaunches > 0) {
                successfulLaunches.toDouble() / totalLaunches * 100
            } else {
                0.0
            }
    }
}
