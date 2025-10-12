package com.example.todoapp.presentation.delegates

class SignUpFormState {
    var email: String by EmailProperty()
    var password: String by PasswordProperty()
    var confirmPassword: String by ConfirmPasswordProperty({ passwordProperty })

    val isValid: Boolean
        get() = emailProperty.isValid() && passwordProperty.isValid() && confirmPasswordProperty.isValid()

    fun shouldShowErrors(): Boolean =
        emailProperty.shouldShowError() || passwordProperty.shouldShowError() || confirmPasswordProperty.shouldShowError()

    fun markAllAsTouched() {
        emailProperty.markAsTouched()
        passwordProperty.markAsTouched()
        confirmPasswordProperty.markAsTouched()
    }

    fun clear() {
        emailProperty.clear()
        passwordProperty.clear()
        confirmPasswordProperty.clear()
    }

    private val emailProperty = EmailProperty()
    private val passwordProperty = PasswordProperty()
    private val confirmPasswordProperty = ConfirmPasswordProperty({ passwordProperty })
}
