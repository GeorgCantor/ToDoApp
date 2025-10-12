package com.example.todoapp.presentation.delegates

import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

class ConfirmPasswordProperty(
    private val passwordProperty: () -> PasswordProperty,
    initialValue: String = "",
) : ValidatedProperty<String>(initialValue) {
    override fun isValid(): Boolean = value == passwordProperty().getValue(null, mockProperty)

    override fun getErrorMessage(): String? =
        when {
            value.isEmpty() -> "Please confirm your password"
            !isValid() -> "Passwords do not match"
            else -> null
        }

    override fun getInitialValue(): String = ""

    companion object {
        val mockProperty =
            object : KProperty<String> {
                override val name: String = "mock"
                override val annotations: List<Annotation> = emptyList()
                override val getter: KProperty.Getter<String> get() = throw NotImplementedError()
                override val isAbstract: Boolean = false
                override val isConst: Boolean = false
                override val isFinal: Boolean = false
                override val isLateinit: Boolean = false
                override val isOpen: Boolean = false
                override val isSuspend: Boolean = false
                override val returnType: kotlin.reflect.KType get() = throw NotImplementedError()
                override val typeParameters: List<KTypeParameter> = emptyList()
                override val visibility: KVisibility get() = throw NotImplementedError()
                override val parameters: List<KParameter> get() = emptyList()

                override fun call(vararg args: Any?): String = throw NotImplementedError()

                override fun callBy(args: Map<KParameter, Any?>): String = throw NotImplementedError()
            }
    }
}
