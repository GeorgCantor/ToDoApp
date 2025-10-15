package com.example.todoapp.presentation.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuthViewModel: AuthViewModel = mockk(relaxed = true)
    private val mockOnSignUpClick: () -> Unit = mockk(relaxed = true)
    private val mockOnForgotPasswordClick: () -> Unit = mockk(relaxed = true)

    @Test
    fun loginScreen_shouldDisplayAllUIElements() {
    }
}
