package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class CalculateTotalUseCase {
    operator fun invoke(items: List<CartItem>): Double = items.sumOf { it.price * it.quantity }

    fun formatPrice(price: Double): String = NumberFormat.getNumberInstance(Locale("ru", "RU")).format(price) + " ₽"
}
