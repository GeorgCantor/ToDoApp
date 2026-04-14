package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.usecase.GetThemeColorUseCase
import com.example.todoapp.domain.usecase.SaveThemeColorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val getThemeColorUseCase: GetThemeColorUseCase,
    private val saveThemeColorUseCase: SaveThemeColorUseCase,
) : ViewModel() {
    private val _themeColor = MutableStateFlow(0xFF6200EE.toInt())
    val themeColor = _themeColor.asStateFlow()

    init {
        viewModelScope.launch {
            getThemeColorUseCase().collect { _themeColor.value = it }
        }
    }

    fun updateThemeColor(colorInt: Int) {
        viewModelScope.launch {
            saveThemeColorUseCase(colorInt)
        }
    }
}
