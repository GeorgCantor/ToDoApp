package com.example.todoapp.domain.manager

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow

interface BiometricAuthManager {
    fun isBiometricAvailable(): Boolean

    suspend fun authenticate(activity: FragmentActivity): Flow<BiometricAuthResult>
}
