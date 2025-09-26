package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.CalculatorOperation
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.repository.CalculatorRepository

class CalculatorRepositoryImpl : CalculatorRepository {
    override fun calculate(
        currentState: CalculatorState,
        input: String,
    ): CalculatorState =
        when {
            input == "C" -> clear()
            input == "<" -> deleteLastCharacter(currentState)
            input == "=" -> performCalculation(currentState)
            input in listOf("+", "-", "×", "÷") -> setOperator(currentState, input)
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

    private fun performCalculation(currentState: CalculatorState): CalculatorState {
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
            )
        } catch (e: ArithmeticException) {
            CalculatorState("Error")
        }
    }

    private fun setOperator(
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
                else -> return currentState
            }

        return CalculatorState(
            displayValue = currentState.displayValue,
            firstOperand = currentValue,
            operator = operator,
            waitingForNewOperand = true,
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
        if (currentState.waitingForNewOperand) {
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
        if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            String.format("%.8f", result).trimEnd('0').trimEnd('.')
        }
}
