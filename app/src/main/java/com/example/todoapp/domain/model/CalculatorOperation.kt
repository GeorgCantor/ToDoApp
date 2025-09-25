package com.example.todoapp.domain.model

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")

    fun execute(first: Double, second: Double): Double {
        return when (this) {
            is Add -> first + second
            is Subtract -> first - second
            is Multiply -> first * second
            is Divide -> {
                if (second == 0.0) throw ArithmeticException("Division by zero")
                first / second
            }
        }
    }
}