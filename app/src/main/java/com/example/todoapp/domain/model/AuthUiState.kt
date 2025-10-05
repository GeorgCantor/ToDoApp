package com.example.todoapp.domain.model

sealed class AuthUiState {
    object Loading : AuthUiState()

    object Unauthenticated : AuthUiState()

    object Authenticated : AuthUiState()

    object PasswordResetSent : AuthUiState()

    data class Error(
        val message: String,
    ) : AuthUiState()
}
