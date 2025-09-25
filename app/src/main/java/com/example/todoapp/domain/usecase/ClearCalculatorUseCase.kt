package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.repository.CalculatorRepository

class ClearCalculatorUseCase(
    private val repository: CalculatorRepository,
) {
    operator fun invoke(): CalculatorState = repository.clear()
}
