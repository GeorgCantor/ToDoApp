package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.UserPreferences
import com.example.todoapp.domain.model.UserProfile
import com.example.todoapp.domain.usecase.GetUserProfileUseCase
import com.example.todoapp.domain.usecase.InitializeUserProfileUseCase
import com.example.todoapp.domain.usecase.SaveUserProfileUseCase
import com.example.todoapp.domain.usecase.UpdateUserStatisticsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val updateUserStatisticsUseCase: UpdateUserStatisticsUseCase,
    private val initializeUserProfileUseCase: InitializeUserProfileUseCase,
) : ViewModel() {
    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProfile()
    }

    fun initializeProfile(email: String) {
        viewModelScope.launch {
            initializeUserProfileUseCase(email)
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                saveUserProfileUseCase(profile.copy(lastSeen = System.currentTimeMillis()))
                _saveSuccess.value = true
                viewModelScope.launch {
                    delay(2000)
                    _saveSuccess.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePreferences(preferences: UserPreferences) {
        _profileState.value?.let { currentProfile ->
            saveProfile(currentProfile.copy(preferences = preferences))
        }
    }

    fun trackNewsRead() {
        viewModelScope.launch {
            updateUserStatisticsUseCase { stats ->
                stats.copy(
                    newsRead = stats.newsRead + 1,
                    lastActive = System.currentTimeMillis(),
                )
            }
        }
    }

    fun trackMessageSent() {
        viewModelScope.launch {
            updateUserStatisticsUseCase { stats ->
                stats.copy(
                    messagesSent = stats.messagesSent + 1,
                    lastActive = System.currentTimeMillis(),
                )
            }
        }
    }

    fun trackCalculation() {
        viewModelScope.launch {
            updateUserStatisticsUseCase { stats ->
                stats.copy(
                    calculationsMade = stats.calculationsMade + 1,
                    lastActive = System.currentTimeMillis(),
                )
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                _profileState.value = profile
            }
        }
    }
}
