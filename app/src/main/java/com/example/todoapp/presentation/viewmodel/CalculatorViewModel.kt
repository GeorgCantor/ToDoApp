package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase
import com.example.todoapp.domain.usecase.UpdateUserStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val calculateExpressionUseCase: CalculateExpressionUseCase,
    private val clearCalculatorUseCase: ClearCalculatorUseCase,
    private val updateUserStatisticsUseCase: UpdateUserStatisticsUseCase,
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
            if (input == "=") trackCalculation()
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Calculation error"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun trackCalculation() {
        viewModelScope.launch {
            updateUserStatisticsUseCase { stats ->
                stats.copy(
                    calculationsMade = stats.calculationsMade + 1,
                    lastActive = System.currentTimeMillis(),
                )
            }
        }
    }
}
