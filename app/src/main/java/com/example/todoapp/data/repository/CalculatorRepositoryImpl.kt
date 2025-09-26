package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.CalculatorOperation
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.repository.CalculatorRepository
import kotlin.math.abs

class CalculatorRepositoryImpl : CalculatorRepository {
    override fun calculate(
        currentState: CalculatorState,
        input: String,
    ): CalculatorState =
        when {
            input == "C" -> clear()
            input == "<" -> deleteLastCharacter(currentState)
            input == "=" -> performCalculation(currentState)
            input == "SCI" -> toggleScientificMode(currentState)
            input == "M+" -> addToMemory(currentState)
            input == "M-" -> subtractFromMemory(currentState)
            input == "MR" -> recallMemory(currentState)
            input == "MC" -> clearMemory(currentState)
            input in listOf("+", "-", "×", "÷", "^") -> setBinaryOperator(currentState, input)
            input in listOf("√", "sin", "cos", "tan", "log", "ln", "!") ->
                performUnaryOperation(
                    currentState,
                    input,
                )

            input in listOf("π", "e") -> setConstant(currentState, input)
            input == "." -> handleDecimalPoint(currentState)
            input.matches(Regex("[0-9]")) -> handleDigit(currentState, input)
            else -> currentState
        }

    override fun clear(): CalculatorState = CalculatorState()

    override fun deleteLastCharacter(currentState: CalculatorState): CalculatorState =
        if (currentState.displayValue.length > 1) {
            currentState.copy(displayValue = currentState.displayValue.dropLast(1))
        } else {
            currentState.copy(displayValue = "0")
        }

    private fun toggleScientificMode(currentState: CalculatorState): CalculatorState =
        currentState.copy(isScientificMode = !currentState.isScientificMode)

    private fun addToMemory(currentState: CalculatorState): CalculatorState {
        val currentValue = currentState.displayValue.toDoubleOrNull() ?: return currentState
        return currentState.copy(memory = currentState.memory + currentValue)
    }

    private fun subtractFromMemory(currentState: CalculatorState): CalculatorState {
        val currentValue = currentState.displayValue.toDoubleOrNull() ?: return currentState
        return currentState.copy(memory = currentState.memory - currentValue)
    }

    private fun recallMemory(currentState: CalculatorState): CalculatorState =
        currentState.copy(
            displayValue = formatResult(currentState.memory),
            waitingForNewOperand = true,
        )

    private fun clearMemory(currentState: CalculatorState): CalculatorState = currentState.copy(memory = 0.0)

    private fun performCalculation(currentState: CalculatorState): CalculatorState {
        if (currentState.operator is CalculatorOperation.Pi || currentState.operator is CalculatorOperation.E) {
            return currentState.copy(
                displayValue = formatResult(currentState.operator.execute(0.0)),
                waitingForNewOperand = true,
            )
        }

        val currentValue = currentState.displayValue.toDoubleOrNull() ?: return currentState
        val firstOperand = currentState.firstOperand ?: return currentState
        val operator = currentState.operator ?: return currentState

        return try {
            val result = operator.execute(firstOperand, currentValue)
            CalculatorState(
                displayValue = formatResult(result),
                firstOperand = null,
                operator = null,
                waitingForNewOperand = true,
                isScientificMode = currentState.isScientificMode,
                memory = currentState.memory,
            )
        } catch (e: ArithmeticException) {
            CalculatorState(displayValue = "Error")
        }
    }

    private fun setBinaryOperator(
        currentState: CalculatorState,
        operatorSymbol: String,
    ): CalculatorState {
        val currentValue = currentState.displayValue.toDoubleOrNull() ?: return currentState

        val operator =
            when (operatorSymbol) {
                "+" -> CalculatorOperation.Add
                "-" -> CalculatorOperation.Subtract
                "×" -> CalculatorOperation.Multiply
                "÷" -> CalculatorOperation.Divide
                "^" -> CalculatorOperation.Power
                else -> return currentState
            }

        return CalculatorState(
            displayValue = currentState.displayValue,
            firstOperand = currentValue,
            operator = operator,
            waitingForNewOperand = true,
            isScientificMode = currentState.isScientificMode,
            memory = currentState.memory,
        )
    }

    private fun performUnaryOperation(
        currentState: CalculatorState,
        operation: String,
    ): CalculatorState {
        val currentValue = currentState.displayValue.toDoubleOrNull() ?: return currentState

        val operator =
            when (operation) {
                "√" -> CalculatorOperation.SquareRoot
                "sin" -> CalculatorOperation.Sin
                "cos" -> CalculatorOperation.Cos
                "tan" -> CalculatorOperation.Tan
                "log" -> CalculatorOperation.Logarithm
                "ln" -> CalculatorOperation.NaturalLog
                "!" -> CalculatorOperation.Factorial
                else -> return currentState
            }

        return try {
            val result = operator.execute(currentValue)
            CalculatorState(
                displayValue = formatResult(result),
                waitingForNewOperand = true,
                isScientificMode = currentState.isScientificMode,
                memory = currentState.memory,
            )
        } catch (e: ArithmeticException) {
            CalculatorState(displayValue = "Error")
        }
    }

    private fun setConstant(
        currentState: CalculatorState,
        constant: String,
    ): CalculatorState {
        val operator =
            when (constant) {
                "π" -> CalculatorOperation.Pi
                "e" -> CalculatorOperation.E
                else -> return currentState
            }

        return CalculatorState(
            displayValue = formatResult(operator.execute(0.0)),
            operator = operator,
            waitingForNewOperand = true,
            isScientificMode = currentState.isScientificMode,
            memory = currentState.memory,
        )
    }

    private fun handleDecimalPoint(currentState: CalculatorState): CalculatorState =
        if (currentState.waitingForNewOperand) {
            currentState.copy(
                displayValue = "0.",
                waitingForNewOperand = false,
            )
        } else if (!currentState.displayValue.contains(".")) {
            currentState.copy(
                displayValue = currentState.displayValue + ".",
                waitingForNewOperand = false,
            )
        } else {
            currentState
        }

    private fun handleDigit(
        currentState: CalculatorState,
        digit: String,
    ): CalculatorState =
        if (currentState.waitingForNewOperand || currentState.displayValue == "Error") {
            currentState.copy(
                displayValue = digit,
                waitingForNewOperand = false,
            )
        } else {
            currentState.copy(
                displayValue = if (currentState.displayValue == "0") digit else currentState.displayValue + digit,
            )
        }

    private fun formatResult(result: Double): String =
        if (result.isInfinite() || result.isNaN()) {
            "Error"
        } else if (result % 1 == 0.0 && abs(result) < 1e10) {
            result.toLong().toString()
        } else {
            String.format("%.10f", result).trimEnd('0').trimEnd('.')
        }
}
