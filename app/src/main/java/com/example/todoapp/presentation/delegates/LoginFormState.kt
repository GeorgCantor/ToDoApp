package com.example.todoapp.presentation.delegates

class LoginFormState {
    val email = EmailProperty()
    val password = PasswordProperty()

    val isValid: Boolean
        get() = email.isValid() && password.isValid()

    fun shouldShowErrors(): Boolean = email.shouldShowError() || password.shouldShowError()

    fun markAllAsTouched() {
        email.markAsTouched()
        password.markAsTouched()
    }

    fun clear() {
        email.clear()
        password.clear()
    }
}
