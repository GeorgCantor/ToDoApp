package com.example.todoapp.presentation.delegates

class ConfirmPasswordProperty(
    private val passwordProperty: () -> PasswordProperty,
    initialValue: String = "",
) : ValidatedProperty<String>(initialValue) {
    override fun isValid(): Boolean = value == passwordProperty().value

    override fun getErrorMessage(): String? =
        when {
            value.isEmpty() -> "Please confirm your password"
            !isValid() -> "Passwords do not match"
            else -> null
        }

    override fun isEmpty(): Boolean = value.isEmpty()

    override fun getInitialValue(): String = ""
}
