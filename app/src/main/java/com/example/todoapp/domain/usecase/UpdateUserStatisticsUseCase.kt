package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.UserStatistics
import com.example.todoapp.domain.repository.UserProfileRepository

class UpdateUserStatisticsUseCase(
    private val repository: UserProfileRepository,
) {
    suspend operator fun invoke(update: (UserStatistics) -> UserStatistics) {
        repository.updateStatistics(update)
    }
}
