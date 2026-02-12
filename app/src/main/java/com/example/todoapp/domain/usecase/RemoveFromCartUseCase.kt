package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.CartRepository

class RemoveFromCartUseCase(
    private val repository: CartRepository,
) {
    suspend operator fun invoke(itemId: Int) = repository.removeItem(itemId)
}
