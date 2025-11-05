package com.example.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.example.todoapp.domain.model.UserPreferences
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.model.UserStatistics
import com.example.todoapp.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class UserProfileRepositoryImpl(
    private val dataStore: DataStore<UserProfile>,
) : UserProfileRepository {
    override suspend fun saveProfile(profile: UserProfile) {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(): UserProfile? {
        TODO("Not yet implemented")
    }

    override fun observeProfile(): Flow<UserProfile?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatistics(statistics: (UserStatistics) -> UserStatistics) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePreferences(preferences: (UserPreferences) -> UserPreferences) {
        TODO("Not yet implemented")
    }

    override suspend fun clearProfile() {
        TODO("Not yet implemented")
    }
}
