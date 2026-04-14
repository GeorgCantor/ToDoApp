package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ThemeRepository

class SaveThemeColorUseCase(
    private val repository: ThemeRepository,
) {
    suspend operator fun invoke(colorInt: Int) {
        repository.saveThemeColor(colorInt)
    }
}
