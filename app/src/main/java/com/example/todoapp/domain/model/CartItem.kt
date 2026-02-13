package com.example.todoapp.domain.model

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String,
    val quantity: Int = 1,
    val badges: List<BadgeType> = emptyList(),
    val isAvailable: Boolean = true,
)

enum class BadgeType {
    PROMOTION,
    NEW,
    BESTSELLER,
}
