package com.example.todoapp.presentation.delegates

class SignUpFormState {
    val email = EmailProperty()
    val password = PasswordProperty()
    val confirmPassword = ConfirmPasswordProperty({ password })

    val isValid: Boolean
        get() = email.isValid() && password.isValid() && confirmPassword.isValid()

    fun shouldShowErrors(): Boolean = email.shouldShowError() || password.shouldShowError() || confirmPassword.shouldShowError()

    fun markAllAsTouched() {
        email.markAsTouched()
        password.markAsTouched()
        confirmPassword.markAsTouched()
    }

    fun clear() {
        email.clear()
        password.clear()
        confirmPassword.clear()
    }
}
