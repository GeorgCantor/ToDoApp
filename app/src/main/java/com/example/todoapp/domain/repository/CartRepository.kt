package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.CartItem

interface CartRepository {
    suspend fun getCartItems(): List<CartItem>

    suspend fun updateQuantity(
        itemId: Int,
        quantity: Int,
    )

    suspend fun removeItem(itemId: Int)

    suspend fun addItem(item: CartItem)

    suspend fun clearCart()
}
