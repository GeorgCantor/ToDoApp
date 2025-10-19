package com.example.todoapp.domain.manager

import kotlinx.coroutines.flow.Flow

interface BiometricAuthManager {
    fun isBiometricAvailable(): Boolean

    suspend fun authenticate(): Flow<BiometricAuthResult>
}
