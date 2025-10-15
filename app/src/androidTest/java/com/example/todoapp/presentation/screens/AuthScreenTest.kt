package com.example.todoapp.presentation.screens

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.presentation.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun authScreen_shouldDisplayAllUIElements() {
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Todo App").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Todo App").assertExists()
        composeTestRule.onNodeWithText("Welcome!").assertExists()
        composeTestRule.onNodeWithText("Log In").assertExists()
        composeTestRule.onNodeWithText("Sign Up").assertExists()
    }

    @Test
    fun whenClickLogIn_shouldNavigateToLoginScreen() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Log In").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithText("Welcome Back").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Welcome Back").assertExists()
    }
}
