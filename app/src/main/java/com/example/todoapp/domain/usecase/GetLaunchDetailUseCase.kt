package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.SpaceXRepository

class GetLaunchDetailUseCase(
    private val repository: SpaceXRepository,
) {
    suspend operator fun invoke(id: String) = repository.getLaunchDetail(id)
}
