package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.manager.BiometricAuthResult
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.domain.model.BiometricAuthState
import com.example.todoapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _biometricState = MutableStateFlow<BiometricAuthState>(BiometricAuthState.Idle)
    val biometricState: StateFlow<BiometricAuthState> = _biometricState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthState().collect { isAuthenticated ->
                _isAuthenticated.value = isAuthenticated
                _uiState.value =
                    if (isAuthenticated) AuthUiState.Authenticated else AuthUiState.Unauthenticated
            }
        }
    }

    fun signIn(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signInWithEmailAndPassword(email, password)
            _uiState.value =
                if (result.isSuccess) {
                    AuthUiState.Authenticated
                } else {
                    AuthUiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
                }
        }
    }

    fun signUp(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signUpWithEmailAndPassword(email, password)
            _uiState.value =
                if (result.isSuccess) {
                    AuthUiState.Authenticated
                } else {
                    AuthUiState.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.resetPassword(email)
            _uiState.value =
                if (result.isSuccess) {
                    AuthUiState.PasswordResetSent
                } else {
                    AuthUiState.Error(result.exceptionOrNull()?.message ?: "Password reset failed")
                }
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun isBiometricAvailable() = authRepository.isBiometricAvailable()

    fun authenticateWithBiometric() {
        _biometricState.value = BiometricAuthState.Loading
        viewModelScope.launch {
            authRepository.authenticateWithBiometric().collect { result ->
                when (result) {
                    is BiometricAuthResult.Success -> {
                        _biometricState.value = BiometricAuthState.Success
                    }
                    is BiometricAuthResult.Error -> {
                        _biometricState.value = BiometricAuthState.Error(result.message)
                    }
                }
            }
        }
    }
}
