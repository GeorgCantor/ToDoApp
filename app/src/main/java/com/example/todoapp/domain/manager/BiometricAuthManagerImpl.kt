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

    override suspend fun authenticate(activity: FragmentActivity): Flow<BiometricAuthResult> =
        callbackFlow {
            if (activity.isFinishing || activity.isDestroyed) {
                trySend(BiometricAuthResult.Error("Activity not available"))
                close()
                return@callbackFlow
            }

            val executor = ContextCompat.getMainExecutor(activity)

            val promptInfo =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .setTitle("Вход в приложение")
                    .setSubtitle("Подтвердите вашу личность")
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
                            val errorMessage =
                                when (errorCode) {
                                    BiometricPrompt.ERROR_USER_CANCELED,
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    -> "Аутентификация отменена"
                                    BiometricPrompt.ERROR_LOCKOUT -> "Слишком много попыток. Попробуйте позже"
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "Биометрия заблокирована. Используйте пароль"
                                    else -> "Ошибка: $errString"
                                }
                            trySend(BiometricAuthResult.Error(errorMessage))
                            close()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            trySend(BiometricAuthResult.Success)
                            close()
                        }

                        override fun onAuthenticationFailed() {
                            trySend(BiometricAuthResult.Error("Отпечаток не распознан. Попробуйте еще раз"))
                        }
                    },
                )

            biometricPrompt.authenticate(promptInfo)

            awaitClose {
                if (!activity.isFinishing && !activity.isDestroyed) {
                    try {
                        biometricPrompt.cancelAuthentication()
                    } catch (e: Exception) {
                    }
                }
            }
        }
}
