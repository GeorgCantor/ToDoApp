package com.example.todoapp.presentation.delegates

class ForgotPasswordFormState {
    val email = EmailProperty()

    val isValid: Boolean
        get() = email.isValid()

    fun shouldShowErrors(): Boolean = email.shouldShowError()

    fun markAsTouched() {
        email.markAsTouched()
    }

    fun clear() {
        email.clear()
    }
}
