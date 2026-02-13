package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.CartRepository

class UpdateQuantityUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(
        itemId: Int,
        quantity: Int,
    ) {
        if (quantity == 0) {
            repository.removeItem(itemId)
        } else {
            repository.updateQuantity(itemId, quantity)
        }
    }
}
