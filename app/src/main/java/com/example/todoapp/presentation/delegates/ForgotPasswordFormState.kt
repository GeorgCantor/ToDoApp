package com.example.todoapp.presentation.delegates

class ForgotPasswordFormState {
    private val _emailDelegate = EmailProperty()

    var email: String by _emailDelegate

    val emailProperty: EmailProperty get() = _emailDelegate

    val isValid: Boolean
        get() = _emailDelegate.isValid()

    fun shouldShowErrors(): Boolean = _emailDelegate.shouldShowError()

    fun markAsTouched() {
        _emailDelegate.markAsTouched()
    }

    fun clear() {
        _emailDelegate.clear()
    }
}
