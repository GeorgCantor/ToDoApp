package com.example.todoapp.presentation.delegates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.todoapp.presentation.delegates.ConfirmPasswordProperty.Companion.mockProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ValidatedProperty<T>(
    initialValue: T,
) : ReadWriteProperty<Any?, T> {
    protected val state: MutableState<T> = mutableStateOf(initialValue)
    private var isTouched: Boolean = false

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = state.value

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T,
    ) {
        state.value = value
        isTouched = true
        onValueChanged(value)
    }

    fun asState(): MutableState<T> = state

    val value: T
        get() = state.value

    fun updateValue(newValue: T) {
        setValue(null, mockProperty, newValue)
    }

    open fun onValueChanged(newValue: T) {}

    abstract fun isValid(): Boolean

    abstract fun getErrorMessage(): String?

    fun shouldShowError(): Boolean = isTouched && !isValid()

    fun markAsTouched() {
        isTouched = true
    }

    fun clear() {
        state.value = getInitialValue()
        isTouched = false
    }

    protected abstract fun getInitialValue(): T
}
