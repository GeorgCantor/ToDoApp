package com.example.todoapp.data.repository

import androidx.fragment.app.FragmentActivity
import com.example.todoapp.domain.manager.BiometricAuthManager
import com.example.todoapp.domain.manager.BiometricAuthResult
import com.example.todoapp.domain.model.User
import com.example.todoapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val biometricAuthManager: BiometricAuthManager,
) : AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    override val currentUser: User?
        get() = auth.currentUser?.toDomainUser()

    override fun getAuthState(): Flow<Boolean> =
        callbackFlow {
            val authStateListener =
                FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser != null)
                }
            auth.addAuthStateListener(authStateListener)
            awaitClose { auth.removeAuthStateListener(authStateListener) }
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override fun isBiometricAvailable() = biometricAuthManager.isBiometricAvailable()

    override suspend fun authenticateWithBiometric(activity: FragmentActivity): Flow<BiometricAuthResult> =
        biometricAuthManager.authenticate(activity)

    private fun FirebaseUser.toDomainUser(): User =
        User(
            id = uid,
            email = email.orEmpty(),
            displayName = displayName ?: email?.substringBefore("@").orEmpty(),
            photoUrl = photoUrl?.toString(),
        )
}
