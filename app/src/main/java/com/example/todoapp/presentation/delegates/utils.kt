package com.example.todoapp.presentation.delegates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberLoginFormState(): LoginFormState = remember { LoginFormState() }

@Composable
fun rememberSignUpFormState(): SignUpFormState = remember { SignUpFormState() }

@Composable
fun rememberForgotPasswordFormState(): ForgotPasswordFormState = remember { ForgotPasswordFormState() }
