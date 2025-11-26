package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.repository.UserProfileRepository

class InitializeUserProfileUseCase(
    private val repository: UserProfileRepository,
) {
    suspend operator fun invoke(email: String) {
        repository.getProfile().email.takeIf { it.isNotBlank() }?.let {
            repository.saveProfile(
                UserProfile(
                    id = System.currentTimeMillis().toString(),
                    email = email,
                    displayName = email.substringBefore("@"),
                    joinDate = System.currentTimeMillis(),
                    lastSeen = System.currentTimeMillis(),
                ),
            )
        }
    }
}
