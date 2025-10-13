package com.example.todoapp.presentation.delegates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ValidatedProperty<T>(
    initialValue: T,
) : ReadWriteProperty<Any?, T> {
    private val _state: MutableState<T> = mutableStateOf(initialValue)
    private var _isTouched: Boolean = false

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = _state.value

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T,
    ) {
        _state.value = value
        _isTouched = true
        onValueChanged(value)
    }

    val state: MutableState<T> get() = _state

    val value: T get() = _state.value
    val isTouched: Boolean get() = _isTouched

    open fun onValueChanged(newValue: T) {}

    abstract fun isValid(): Boolean

    abstract fun getErrorMessage(): String?

    abstract fun isEmpty(): Boolean

    fun shouldShowError(): Boolean = _isTouched && !isValid() && !isEmpty()

    fun markAsTouched() {
        _isTouched = true
    }

    fun clear() {
        _state.value = getInitialValue()
        _isTouched = false
    }

    protected abstract fun getInitialValue(): T
}
