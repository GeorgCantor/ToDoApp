package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.UserPreferences
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.model.UserStatistics
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun saveProfile(profile: UserProfile)

    suspend fun getProfile(): UserProfile?

    fun observeProfile(): Flow<UserProfile?>

    suspend fun updateStatistics(statistics: (UserStatistics) -> UserStatistics)

    suspend fun updatePreferences(preferences: (UserPreferences) -> UserPreferences)

    suspend fun clearProfile()
}
