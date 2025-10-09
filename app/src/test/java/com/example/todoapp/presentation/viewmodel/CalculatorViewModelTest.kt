package com.example.todoapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.todoapp.domain.model.CalculatorOperation
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CalculatorViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CalculatorViewModel
    private val calculateExpressionUseCase: CalculateExpressionUseCase = mockk()
    private val clearCalculatorUseCase: ClearCalculatorUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CalculatorViewModel(calculateExpressionUseCase, clearCalculatorUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize with empty calculator state`() =
        runTest {
            viewModel.calculatorState.test {
                assertEquals(CalculatorState(), awaitItem())
            }
            viewModel.errorMessage.test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `should initialize with default calculator state`() =
        runTest {
            viewModel.calculatorState.test {
                val initialState = awaitItem()
                assertEquals("0", initialState.displayValue)
                assertNull(initialState.firstOperand)
                assertNull(initialState.operator)
                assertFalse(initialState.waitingForNewOperand)
                assertFalse(initialState.isScientificMode)
            }

            viewModel.errorMessage.test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `should clear calculator when C button is clicked`() =
        runTest {
            val clearedState = CalculatorState(displayValue = "0")
            every { clearCalculatorUseCase() } returns clearedState

            viewModel.onButtonClick("C")

            verify { clearCalculatorUseCase() }
            viewModel.calculatorState.test {
                assertEquals(clearedState, awaitItem())
            }
            viewModel.errorMessage.test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `should set error message when calculation throws exception`() =
        runTest {
            val exceptionMessage = "Invalid expression"
            every { calculateExpressionUseCase(any(), "+") } throws RuntimeException(exceptionMessage)

            viewModel.onButtonClick("+")
            viewModel.errorMessage.test {
                assertEquals(exceptionMessage, awaitItem())
            }
        }

    @Test
    fun `should handle digit input correctly`() =
        runTest {
            val currentState = CalculatorState(displayValue = "5", firstOperand = 5.0)
            val newState = CalculatorState(displayValue = "57", firstOperand = 57.0)
            every { calculateExpressionUseCase(currentState, "7") } returns newState

            viewModel.onButtonClick("7")

            verify { calculateExpressionUseCase(any(), "7") }
        }

    @Test
    fun `should handle operator input correctly`() =
        runTest {
            val currentState = CalculatorState(displayValue = "5", firstOperand = 5.0)
            val newState =
                CalculatorState(
                    displayValue = "5+",
                    firstOperand = 5.0,
                    operator = CalculatorOperation.Add,
                    waitingForNewOperand = true,
                )
            every { calculateExpressionUseCase(currentState, "+") } returns newState

            viewModel.onButtonClick("+")

            verify { calculateExpressionUseCase(any(), "+") }
        }

    @Test
    fun `should handle equals operation correctly`() =
        runTest {
            val currentState =
                CalculatorState(
                    displayValue = "5+3",
                    firstOperand = 5.0,
                    operator = CalculatorOperation.Add,
                    waitingForNewOperand = true,
                )
            val resultState =
                CalculatorState(
                    displayValue = "8",
                    firstOperand = 8.0,
                    operator = null,
                    waitingForNewOperand = false,
                )
            every { calculateExpressionUseCase(currentState, "=") } returns resultState

            viewModel.onButtonClick("=")

            verify { calculateExpressionUseCase(any(), "=") }
        }
}
