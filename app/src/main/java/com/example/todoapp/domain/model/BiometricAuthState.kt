package com.example.todoapp.domain.model

sealed class BiometricAuthState {
    object Idle : BiometricAuthState()

    object Loading : BiometricAuthState()

    object Success : BiometricAuthState()

    data class Error(
        val message: String,
    ) : BiometricAuthState()
}
