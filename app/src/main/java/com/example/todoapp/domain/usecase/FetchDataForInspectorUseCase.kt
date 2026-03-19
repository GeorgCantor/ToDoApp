package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.SpaceXRepository

class FetchDataForInspectorUseCase(
    private val repository: SpaceXRepository,
) {
    suspend operator fun invoke(): Result<Any> =
        try {
            Result.success(repository.getLaunches())
        } catch (e: Exception) {
            Result.failure(e)
        }
}
