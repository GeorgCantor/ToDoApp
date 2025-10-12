package com.example.todoapp.presentation.delegates

class EmailProperty(initialValue: String) : ValidatedProperty<String>(initialValue) {
    override fun isValid(): Boolean {
        return value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))
    }

    override fun getErrorMessage(): String? {
        return when {
            value.isEmpty() -> "Email is required"
            !isValid() -> "Please enter a valid email address"
            else -> null
        }
    }

    override fun getInitialValue(): String = ""
}