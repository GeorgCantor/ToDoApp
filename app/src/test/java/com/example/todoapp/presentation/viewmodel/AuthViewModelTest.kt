package com.example.todoapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
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

    @Test
    fun `signUp should update state to Error on failure`() =
        runTest {
            val errorMessage = "Registration failed"
            coEvery {
                authRepository.signUpWithEmailAndPassword("new@test.com", "weak")
            } returns Result.failure(Exception(errorMessage))

            viewModel.signUp("new@test.com", "weak")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertTrue(currentState is AuthUiState.Error)
                assertEquals(errorMessage, (currentState as AuthUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `resetPassword should update state to PasswordResetSent on success`() =
        runTest {
            coEvery {
                authRepository.resetPassword("test@test.com")
            } returns Result.success(Unit)

            viewModel.resetPassword("test@test.com")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.PasswordResetSent, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `resetPassword should update state to Error on failure`() =
        runTest {
            val errorMessage = "Password reset failed"
            coEvery {
                authRepository.resetPassword("invalid@test.com")
            } returns Result.failure(Exception(errorMessage))

            viewModel.resetPassword("invalid@test.com")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertTrue(currentState is AuthUiState.Error)
                assertEquals(errorMessage, (currentState as AuthUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `clearError should reset error state to Unauthenticated`() =
        runTest {
            val errorMessage = "Test error"
            coEvery {
                authRepository.signInWithEmailAndPassword(any(), any())
            } returns Result.failure(Exception(errorMessage))

            viewModel.signIn("test", "wrong")

            viewModel.uiState.test {
                val errorState = expectMostRecentItem()
                assertTrue(errorState is AuthUiState.Error)
                cancelAndConsumeRemainingEvents()
            }

            viewModel.clearError()

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `signOut should call repository signOut`() =
        runTest {
            coEvery { authRepository.signOut() } returns Unit
            viewModel.signOut()
            coVerify { authRepository.signOut() }
        }

    @Test
    fun `isAuthenticated should reflect auth state changes`() =
        runTest {
            viewModel.isAuthenticated.test {
                val currentState = expectMostRecentItem()
                assertEquals(false, currentState)
                cancelAndConsumeRemainingEvents()
            }

            authStateFlow.value = true

            viewModel.isAuthenticated.test {
                val currentState = expectMostRecentItem()
                assertEquals(true, currentState)
                cancelAndConsumeRemainingEvents()
            }

            authStateFlow.value = false

            viewModel.isAuthenticated.test {
                val currentState = expectMostRecentItem()
                assertEquals(false, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should handle multiple authentication state changes`() =
        runTest {
            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }

            authStateFlow.value = true

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Authenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }

            authStateFlow.value = false

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should not clear error when not in error state`() =
        runTest {
            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }

            viewModel.clearError()

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertEquals(AuthUiState.Unauthenticated, currentState)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `should handle empty error message`() =
        runTest {
            coEvery {
                authRepository.signInWithEmailAndPassword(any(), any())
            } returns Result.failure(Exception())

            viewModel.signIn("test", "wrong")

            viewModel.uiState.test {
                val currentState = expectMostRecentItem()
                assertTrue(currentState is AuthUiState.Error)
                assertEquals("Sign in failed", (currentState as AuthUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }
}
