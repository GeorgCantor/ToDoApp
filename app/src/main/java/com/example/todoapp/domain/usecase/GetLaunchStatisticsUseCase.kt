package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.LaunchStatistics
import com.example.todoapp.domain.model.SpaceXLaunch
import com.example.todoapp.domain.repository.SpaceXRepository

class GetLaunchStatisticsUseCase(
    private val repository: SpaceXRepository,
) {
    suspend operator fun invoke(limit: Int = 50): Result<LaunchStatistics> {
        return try {
            val launches = repository.getLaunches(limit)
            val statistics = calculateStatistics(launches.getOrThrow())
            return Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateStatistics(launches: List<SpaceXLaunch>): LaunchStatistics =
        LaunchStatistics(
            totalLaunches = launches.size,
            successfulLaunches = launches.count { it.launchSuccess == true },
            failedLaunches = launches.count { it.launchSuccess == false },
            successRate = 0.0,
            launchesByYear = mapOf(),
            launchesByRocket = mapOf(),
        )
}
