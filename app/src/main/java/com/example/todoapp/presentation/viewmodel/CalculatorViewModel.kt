package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalculatorViewModel(
    private val calculateExpressionUseCase: CalculateExpressionUseCase,
    private val clearCalculatorUseCase: ClearCalculatorUseCase,
) : ViewModel() {
    private val _calculatorState = MutableStateFlow(CalculatorState())
    val calculatorState: StateFlow<CalculatorState> = _calculatorState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
