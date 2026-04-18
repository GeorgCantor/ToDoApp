package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ThemeRepository

class GetThemeColorUseCase(
    private val repository: ThemeRepository,
) {
    operator fun invoke() = repository.getThemeColor()
}
