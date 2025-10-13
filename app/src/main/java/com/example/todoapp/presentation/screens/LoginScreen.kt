package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.presentation.delegates.rememberLoginFormState
import com.example.todoapp.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formState = rememberLoginFormState()
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val emailState by formState.emailProperty.state
    val passwordState by formState.passwordProperty.state

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> {
                val errorMessage = (uiState as AuthUiState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
                authViewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = emailState,
                        onValueChange = { formState.email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.emailProperty.shouldShowError(),
                        supportingText = {
                            if (formState.emailProperty.shouldShowError()) {
                                Text(text = formState.emailProperty.getErrorMessage().orEmpty())
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = passwordState,
                        onValueChange = { formState.password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.passwordProperty.shouldShowError(),
                        supportingText = {
                            if (formState.passwordProperty.shouldShowError()) {
                                Text(text = formState.passwordProperty.getErrorMessage().orEmpty())
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            formState.markAllAsTouched()
                            if (formState.isValid) {
                                authViewModel.signIn(
                                    formState.email,
                                    formState.password,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formState.isValid,
                    ) {
                        Text("Sign In")
                    }

                    TextButton(
                        onClick = onForgotPasswordClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Forgot Password?")
                    }

                    TextButton(
                        onClick = onSignUpClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Don't have an account? Sign Up")
                    }
                }
            }
        }
    }
}
