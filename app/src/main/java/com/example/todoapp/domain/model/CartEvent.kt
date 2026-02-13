package com.example.todoapp.domain.model

sealed class CartEvent {
    data class IncrementQuantity(
        val itemId: Int,
        val currentQuantity: Int,
    ) : CartEvent()

    data class DecrementQuantity(
        val itemId: Int,
        val currentQuantity: Int,
    ) : CartEvent()

    data class RemoveItem(
        val itemId: Int,
    ) : CartEvent()

    object Checkout : CartEvent()
}
