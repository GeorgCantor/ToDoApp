package com.example.todoapp.domain.model

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val itemsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)
