package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.repository.CalculatorRepository

class CalculateExpressionUseCase(
    private val repository: CalculatorRepository,
) {
    operator fun invoke(
        currentState: CalculatorState,
        input: String,
    ): CalculatorState = repository.calculate(currentState, input)
}
