package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val repository: UserProfileRepository,
) {
    operator fun invoke(): Flow<UserProfile?> = repository.observeProfile()
}
