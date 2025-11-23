package com.example.todoapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoPath: String = "",
    val bio: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val joinDate: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences(),
    val statistics: UserStatistics = UserStatistics(),
) {
    fun createDefaultProfile(): UserProfile =
        UserProfile(
            displayName = "",
            email = "user@example.com",
            bio = "",
            phoneNumber = "",
            location = "",
        )
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
    val totalSessionTime: Long = 0,
    val sessionsCount: Int = 0,
    val lastSessionDuration: Long = 0,
    val averageSessionTime: Long = 0,
) {
    fun calculateAverageSessionTime(): Long = if (sessionsCount > 0) totalSessionTime / sessionsCount else 0
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM,
    ;

    companion object {
        fun fromString(value: String): AppTheme = entries.find { it.name == value } ?: SYSTEM
    }
}
