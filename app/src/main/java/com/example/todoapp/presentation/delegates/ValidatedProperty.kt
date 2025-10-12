package com.example.todoapp.presentation.delegates

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ValidatedProperty<T>(
    initialValue: T,
) : ReadWriteProperty<Any?, T> {
    var value: T = initialValue
    private var isTouched: Boolean = false

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
    }

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
