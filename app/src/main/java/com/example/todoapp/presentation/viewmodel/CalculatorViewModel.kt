package com.example.todoapp.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase

class CalculatorViewModel(
    private val calculateExpressionUseCase: CalculateExpressionUseCase,
    private val clearCalculatorUseCase: ClearCalculatorUseCase,
) : ViewModel() {
    private val _calculatorState = mutableStateOf(CalculatorState())
    val calculatorState: State<CalculatorState> = _calculatorState

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun onButtonClick(input: String) {
        try {
            when (input) {
                "C" -> _calculatorState.value = clearCalculatorUseCase()
                else ->
                    _calculatorState.value =
                        calculateExpressionUseCase(_calculatorState.value, input)
            }
            clearError()
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Calculation error"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
