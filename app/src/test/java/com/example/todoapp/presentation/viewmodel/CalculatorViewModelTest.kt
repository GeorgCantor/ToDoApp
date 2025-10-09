package com.example.todoapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.todoapp.domain.model.CalculatorState
import com.example.todoapp.domain.usecase.CalculateExpressionUseCase
import com.example.todoapp.domain.usecase.ClearCalculatorUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `should initialize with empty calculator state`() = runTest {
        viewModel.calculatorState.test {
            assertEquals(CalculatorState(), awaitItem())
        }
        viewModel.errorMessage.test {
            assertNull(awaitItem())
        }
    }
}