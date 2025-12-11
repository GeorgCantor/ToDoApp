package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.SpaceXLaunch
import com.example.todoapp.domain.repository.SpaceXRepository

class GetSpaceXLaunchesUseCase(
    private val repository: SpaceXRepository,
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<SpaceXLaunch>> = repository.getLaunches(limit)
}
