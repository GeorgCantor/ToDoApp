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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> {
                val errorMessage = (uiState as AuthUiState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
            }

            is AuthUiState.PasswordResetSent -> {
                snackbarHostState.showSnackbar("Password reset email sent! Check your inbox.")
                delay(2000)
                onBackClick()
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
                        text = "Reset Password",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enter your email address and we'll send you a link to reset your password",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { authViewModel.resetPassword(email) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = email.isNotBlank(),
                    ) {
                        Text("Send Reset Link")
                    }

                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Back to Login")
                    }
                }
            }
        }
    }
}
