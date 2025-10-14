package com.example.todoapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk()
    private val authStateFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { authRepository.getAuthState() } returns authStateFlow
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize with unauthenticated state`() =
        runTest {
            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should update state when authenticated`() =
        runTest {
            authStateFlow.value = true

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Authenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }

            viewModel.isAuthenticated.test {
                val isAuthenticated = expectMostRecentItem()
                assertTrue(isAuthenticated)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `signIn should update state to Authenticated on success`() =
        runTest {
            coEvery {
                authRepository.signInWithEmailAndPassword("test@test.com", "password")
            } returns Result.success(Unit)

            viewModel.signIn("test@test.com", "password")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Authenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `signIn should update state to Error on failure`() =
        runTest {
            val errorMessage = "Invalid credentials"
            coEvery {
                authRepository.signInWithEmailAndPassword("test@test.com", "wrong")
            } returns Result.failure(Exception(errorMessage))

            viewModel.signIn("test@test.com", "wrong")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertTrue(currentState is AuthUiState.Error)
                assertEquals(errorMessage, (currentState as AuthUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `signUp should update state to Authenticated on success`() =
        runTest {
            coEvery {
                authRepository.signUpWithEmailAndPassword("new@test.com", "password")
            } returns Result.success(Unit)

            viewModel.signUp("new@test.com", "password")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Authenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }
}
