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

    @Test
    fun `should clear error message when clearError is called`() =
        runTest {
            every { calculateExpressionUseCase(any(), "invalid") } throws RuntimeException("Error")
            viewModel.onButtonClick("invalid")

            viewModel.errorMessage.test {
                assertEquals("Error", awaitItem())
            }

            viewModel.clearError()

            viewModel.errorMessage.test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `should set default error message when exception has no message`() =
        runTest {
            every { calculateExpressionUseCase(any(), "*") } throws RuntimeException()

            viewModel.onButtonClick("*")

            viewModel.errorMessage.test {
                assertEquals("Calculation error", awaitItem())
            }
        }

    @Test
    fun `should clear error when successful operation follows error`() =
        runTest {
            every { calculateExpressionUseCase(any(), "invalid") } throws RuntimeException("Error")
            viewModel.onButtonClick("invalid")

            viewModel.errorMessage.test {
                assertEquals("Error", awaitItem())
            }

            val clearedState = CalculatorState()
            every { clearCalculatorUseCase() } returns clearedState
            viewModel.onButtonClick("C")

            viewModel.errorMessage.test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `should handle all supported buttons`() =
        runTest {
            val buttons =
                listOf(
                    "0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "+",
                    "-",
                    "*",
                    "/",
                    "=",
                    "C",
                    ".",
                    "sin",
                    "cos",
                    "tan",
                    "log",
                    "ln",
                    "π",
                    "e",
                    "x²",
                    "√",
                    "±",
                    "MC",
                    "MR",
                    "M+",
                    "M-",
                )
            buttons.forEach { button ->
                if (button == "C") {
                    every { clearCalculatorUseCase() } returns CalculatorState()
                } else {
                    every { calculateExpressionUseCase(any(), button) } returns CalculatorState()
                }

                viewModel.onButtonClick(button)

                if (button == "C") {
                    verify { clearCalculatorUseCase() }
                } else {
                    verify { calculateExpressionUseCase(any(), button) }
                }
            }
        }

    @Test
    fun `should preserve previous state when exception occurs`() =
        runTest {
            val initialState = CalculatorState(displayValue = "5", firstOperand = 5.0)
            every { calculateExpressionUseCase(any(), "1") } returns initialState

            viewModel.onButtonClick("1")

            val previousState = viewModel.calculatorState.value

            every { calculateExpressionUseCase(any(), "invalid") } throws RuntimeException("Error")
            viewModel.onButtonClick("invalid")

            assertEquals(previousState, viewModel.calculatorState.value)
            viewModel.errorMessage.test {
                assertEquals("Error", awaitItem())
            }
        }

    @Test
    fun `should handle multiple consecutive operations`() =
        runTest {
            val states =
                listOf(
                    CalculatorState(displayValue = "1", firstOperand = 1.0),
                    CalculatorState(displayValue = "12", firstOperand = 12.0),
                    CalculatorState(
                        displayValue = "12+",
                        firstOperand = 12.0,
                        operator = CalculatorOperation.Add,
                        waitingForNewOperand = true,
                    ),
                    CalculatorState(
                        displayValue = "12+3",
                        firstOperand = 12.0,
                        operator = CalculatorOperation.Add,
                        waitingForNewOperand = true,
                    ),
                    CalculatorState(displayValue = "15", firstOperand = 15.0, operator = null, waitingForNewOperand = false),
                )

            every { calculateExpressionUseCase(any(), "1") } returns states[0]
            viewModel.onButtonClick("1")
            assertEquals(states[0], viewModel.calculatorState.value)

            every { calculateExpressionUseCase(states[0], "2") } returns states[1]
            viewModel.onButtonClick("2")
            assertEquals(states[1], viewModel.calculatorState.value)

            every { calculateExpressionUseCase(states[1], "+") } returns states[2]
            viewModel.onButtonClick("+")
            assertEquals(states[2], viewModel.calculatorState.value)

            every { calculateExpressionUseCase(states[2], "3") } returns states[3]
            viewModel.onButtonClick("3")
            assertEquals(states[3], viewModel.calculatorState.value)

            every { calculateExpressionUseCase(states[3], "=") } returns states[4]
            viewModel.onButtonClick("=")
            assertEquals(states[4], viewModel.calculatorState.value)
        }

    @Test
    fun `should handle scientific operations`() =
        runTest {
            val currentState = CalculatorState(displayValue = "45", firstOperand = 45.0)
            val sinState = CalculatorState(displayValue = "0.7071", firstOperand = 0.7071)
            every { calculateExpressionUseCase(currentState, "sin") } returns sinState

            viewModel.onButtonClick("sin")

            verify { calculateExpressionUseCase(any(), "sin") }
        }

    @Test
    fun `should handle memory operations`() =
        runTest {
            val currentState = CalculatorState(displayValue = "5", firstOperand = 5.0, memory = 0.0)
            val memoryState = CalculatorState(displayValue = "5", firstOperand = 5.0, memory = 5.0)
            every { calculateExpressionUseCase(currentState, "M+") } returns memoryState

            viewModel.onButtonClick("M+")

            verify { calculateExpressionUseCase(any(), "M+") }
        }
}
