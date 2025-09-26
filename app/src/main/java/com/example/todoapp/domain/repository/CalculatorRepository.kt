package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.CalculatorState

interface CalculatorRepository {
    fun calculate(
        currentState: CalculatorState,
        input: String,
    ): CalculatorState

    fun clear(): CalculatorState

    fun deleteLastCharacter(currentState: CalculatorState): CalculatorState
}
