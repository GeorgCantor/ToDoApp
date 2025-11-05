package com.example.todoapp.data.repository

import androidx.datastore.core.DataStore
import com.example.todoapp.domain.model.UserPreferences
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.model.UserStatistics
import com.example.todoapp.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val dataStore: DataStore<UserProfile>,
) : UserProfileRepository {
    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.updateData { profile }
    }

    override suspend fun getProfile(): UserProfile? {
        dataStore.data.map { it }.let { flow ->
            var profile: UserProfile? = null
            flow.collect { profile = it }
            return profile
        }
    }

    override fun observeProfile(): Flow<UserProfile?> = dataStore.data.map { it }

    override suspend fun updateStatistics(statistics: (UserStatistics) -> UserStatistics) {
        dataStore.updateData { currentProfile ->
            currentProfile.copy(
                statistics = statistics(currentProfile.statistics),
                lastSeen = System.currentTimeMillis(),
            )
        }
    }

    override suspend fun updatePreferences(preferences: (UserPreferences) -> UserPreferences) {
        dataStore.updateData { currentProfile ->
            currentProfile.copy(preferences = preferences(currentProfile.preferences))
        }
    }

    override suspend fun clearProfile() {
        dataStore.updateData { UserProfile() }
    }
}
