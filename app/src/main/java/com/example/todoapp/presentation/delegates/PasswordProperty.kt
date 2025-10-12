package com.example.todoapp.presentation.delegates

class PasswordProperty(
    initialValue: String = "",
) : ValidatedProperty<String>(initialValue) {
    override fun isValid(): Boolean = value.length >= 6

    override fun getErrorMessage(): String? =
        when {
            value.isEmpty() -> "Password is required"
            value.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

    override fun getInitialValue(): String = ""
}
