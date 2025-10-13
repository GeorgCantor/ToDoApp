package com.example.todoapp.presentation.delegates

class SignUpFormState {
    private val _emailDelegate = EmailProperty()
    private val _passwordDelegate = PasswordProperty()
    private val _confirmPasswordDelegate = ConfirmPasswordProperty({ _passwordDelegate })

    var email: String by _emailDelegate
    var password: String by _passwordDelegate
    var confirmPassword: String by _confirmPasswordDelegate

    val emailProperty: EmailProperty get() = _emailDelegate
    val passwordProperty: PasswordProperty get() = _passwordDelegate
    val confirmPasswordProperty: ConfirmPasswordProperty get() = _confirmPasswordDelegate

    val isValid: Boolean
        get() = _emailDelegate.isValid() && _passwordDelegate.isValid() && _confirmPasswordDelegate.isValid()

    fun markAllAsTouched() {
        _emailDelegate.markAsTouched()
        _passwordDelegate.markAsTouched()
        _confirmPasswordDelegate.markAsTouched()
    }

    fun clear() {
        _emailDelegate.clear()
        _passwordDelegate.clear()
        _confirmPasswordDelegate.clear()
    }
}
