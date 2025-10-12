package com.example.todoapp.presentation.delegates

class ForgotPasswordFormState {
    var email: String by EmailProperty()

    val isValid: Boolean
        get() = emailProperty.isValid()

    fun shouldShowErrors(): Boolean = emailProperty.shouldShowError()

    fun markAsTouched() {
        emailProperty.markAsTouched()
    }

    fun clear() {
        emailProperty.clear()
    }

    private val emailProperty = EmailProperty()
}
