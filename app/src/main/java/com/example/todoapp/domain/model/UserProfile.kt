package com.example.todoapp.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val joinDate: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences(),
    val statistics: UserStatistics = UserStatistics(),
) {
    fun getJoinDateLocal(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(joinDate), ZoneId.systemDefault())

    fun getLastSeenLocal(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSeen), ZoneId.systemDefault())
}

@Serializable
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val biometricAuthEnabled: Boolean = false,
    val language: String = "en",
    val autoSave: Boolean = true,
)

@Serializable
data class UserStatistics(
    val newsRead: Int = 0,
    val messagesSent: Int = 0,
    val calculationsMade: Int = 0,
    val documentsDownloaded: Int = 0,
    val lastActive: Long = System.currentTimeMillis(),
)

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM,
    ;

    companion object {
        fun fromString(value: String): AppTheme = entries.find { it.name == value } ?: SYSTEM
    }
}
