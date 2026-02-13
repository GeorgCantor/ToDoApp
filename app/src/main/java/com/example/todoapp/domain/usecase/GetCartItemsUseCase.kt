package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.CartItem
import com.example.todoapp.domain.repository.CartRepository

class GetCartItemsUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(): List<CartItem> = repository.getCartItems()
}
