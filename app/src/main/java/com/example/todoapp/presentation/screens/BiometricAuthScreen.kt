package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.todoapp.domain.model.BiometricAuthState
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.AuthViewModel

@Composable
fun BiometricAuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = remember(context) { context as FragmentActivity }
    val biometricState by authViewModel.biometricState.collectAsStateWithLifecycle()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.authenticateWithBiometric(activity)
    }

    LaunchedEffect(biometricState, isAuthenticated) {
        if (biometricState is BiometricAuthState.Success) {
            navController.navigate(if (isAuthenticated) NavRoutes.Splash.route else NavRoutes.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Fingerprint",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Вход в приложение",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Используйте отпечаток пальца для входа",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (biometricState is BiometricAuthState.Loading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ожидание отпечатка пальца...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (biometricState is BiometricAuthState.Error) {
                    val errorMessage = (biometricState as BiometricAuthState.Error).message
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { authViewModel.authenticateWithBiometric(activity) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Повторить")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = {
                        navController.navigate(NavRoutes.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Войти с помощью email и пароля")
                }
            }
        }
    }
}
