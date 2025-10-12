package com.example.todoapp.presentation.delegates

class LoginFormState {
    private val _emailDelegate = EmailProperty()
    private val _passwordDelegate = PasswordProperty()

    var email: String by _emailDelegate
    var password: String by _passwordDelegate

    val emailProperty: EmailProperty get() = _emailDelegate
    val passwordProperty: PasswordProperty get() = _passwordDelegate

    val isValid: Boolean
        get() = _emailDelegate.isValid() && _passwordDelegate.isValid()

    fun shouldShowErrors(): Boolean = _emailDelegate.shouldShowError() || _passwordDelegate.shouldShowError()

    fun markAllAsTouched() {
        _emailDelegate.markAsTouched()
        _passwordDelegate.markAsTouched()
    }

    fun clear() {
        _emailDelegate.clear()
        _passwordDelegate.clear()
    }
}
