package com.example.todoapp.domain.manager

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BiometricAuthManagerImpl(
    private val context: Context,
) : BiometricAuthManager {
    override fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    override suspend fun authenticate(): Flow<BiometricAuthResult> =
        callbackFlow {
            val activity = context as? FragmentActivity
            if (activity == null) {
                trySend(BiometricAuthResult.Error("Context is not FragmentActivity"))
                close()
                return@callbackFlow
            }
            val executor = ContextCompat.getMainExecutor(context)

            val promptInfo =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .setTitle("Вход в приложение")
                    .setSubtitle("Используйте отпечаток пальца для входа")
                    .setNegativeButtonText("Отмена")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()

            val biometricPrompt =
                BiometricPrompt(
                    activity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence,
                        ) {
                            trySend(BiometricAuthResult.Error("Authentication error: $errString"))
                            close()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            trySend(BiometricAuthResult.Success)
                            close()
                        }

                        override fun onAuthenticationFailed() {
                            trySend(BiometricAuthResult.Error("Authentication failed"))
                        }
                    },
                )

            biometricPrompt.authenticate(promptInfo)

            awaitClose {
                biometricPrompt.cancelAuthentication()
            }
        }
}
