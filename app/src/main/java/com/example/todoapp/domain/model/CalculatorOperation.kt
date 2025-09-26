package com.example.todoapp.domain.model

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

sealed class CalculatorOperation(
    val symbol: String,
) {
    object Add : CalculatorOperation("+")

    object Subtract : CalculatorOperation("-")

    object Multiply : CalculatorOperation("×")

    object Divide : CalculatorOperation("÷")

    object Power : CalculatorOperation("^")

    object SquareRoot : CalculatorOperation("√")

    object Logarithm : CalculatorOperation("log")

    object NaturalLog : CalculatorOperation("ln")

    object Sin : CalculatorOperation("sin")

    object Cos : CalculatorOperation("cos")

    object Tan : CalculatorOperation("tan")

    object Factorial : CalculatorOperation("!")

    object Percentage : CalculatorOperation("%")

    object Pi : CalculatorOperation("π")

    object E : CalculatorOperation("e")

    fun execute(
        first: Double,
        second: Double? = null,
    ): Double =
        when (this) {
            is Add -> first + (second ?: 0.0)
            is Subtract -> first - (second ?: 0.0)
            is Multiply -> first * (second ?: 1.0)
            is Divide -> {
                if (second == 0.0) throw ArithmeticException("Division by zero")
                first / (second ?: 1.0)
            }

            is Power -> first.pow(second ?: 2.0)
            is SquareRoot -> sqrt(first)
            is Logarithm -> log10(first)
            is NaturalLog -> ln(first)
            is Sin -> sin(first * PI / 180)
            is Cos -> cos(first * PI / 180)
            is Tan -> tan(first * PI / 180)
            is Factorial -> {
                if (first < 0 || first % 1 != 0.0) throw ArithmeticException("Factorial requires non-negative integer")
                (1..first.toInt()).fold(1.0) { acc, i -> acc * i }
            }

            is Percentage -> first * ((second ?: 0.0) / 100)
            is Pi -> PI
            is E -> Math.E
        }
}
