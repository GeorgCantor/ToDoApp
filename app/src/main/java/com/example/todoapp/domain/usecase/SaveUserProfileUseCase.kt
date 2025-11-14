package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.repository.UserProfileRepository

class SaveUserProfileUseCase(
    private val repository: UserProfileRepository,
) {
    suspend operator fun invoke(profile: UserProfile) = repository.saveProfile(profile)
}
