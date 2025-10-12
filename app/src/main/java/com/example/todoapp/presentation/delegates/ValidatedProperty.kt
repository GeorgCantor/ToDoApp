package com.example.todoapp.presentation.delegates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ValidatedProperty<T>(
    initialValue: T,
) : ReadWriteProperty<Any?, T> {
    protected val state: MutableState<T> = mutableStateOf(initialValue)
    private var isTouched: Boolean = false

    var value: T
        get() = state.value
        set(value) {
            state.value = value
        }

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = value

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T,
    ) {
        this.value = value
        isTouched = true
        onValueChanged(value)
    }

    fun asState(): MutableState<T> = state

    open fun onValueChanged(newValue: T) {}

    abstract fun isValid(): Boolean

    abstract fun getErrorMessage(): String?

    fun shouldShowError(): Boolean = isTouched && !isValid()

    fun markAsTouched() {
        isTouched = true
    }

    fun clear() {
        value = getInitialValue()
        isTouched = false
    }

    protected abstract fun getInitialValue(): T
}
