package com.example.todoapp.domain.model

data class CalculatorState(
    val displayValue: String = "0",
    val firstOperand: Double? = null,
    val operator: CalculatorOperation? = null,
    val waitingForNewOperand: Boolean = false,
)
