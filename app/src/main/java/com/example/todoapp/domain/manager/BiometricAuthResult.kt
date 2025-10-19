package com.example.todoapp.domain.manager

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()

    data class Error(
        val message: String,
    ) : BiometricAuthResult()
}
