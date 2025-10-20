package com.example.todoapp.domain.repository

import androidx.fragment.app.FragmentActivity
import com.example.todoapp.domain.manager.BiometricAuthResult
import com.example.todoapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?

    fun getAuthState(): Flow<Boolean>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun signOut()

    suspend fun resetPassword(email: String): Result<Unit>

    fun isBiometricAvailable(): Boolean

    suspend fun authenticateWithBiometric(activity: FragmentActivity): Flow<BiometricAuthResult>
}
