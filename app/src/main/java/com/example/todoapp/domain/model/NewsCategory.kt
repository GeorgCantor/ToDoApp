package com.example.todoapp.domain.model

enum class NewsCategory(
    val displayName: String,
    val apiValue: String,
) {
    GENERAL("General", "general"),
    BUSINESS("Business", "business"),
    TECHNOLOGY("Technology", "technology"),
    SPORTS("Sports", "sports"),
    ENTERTAINMENT("Entertainment", "entertainment"),
    HEALTH("Health", "health"),
    SCIENCE("Science", "science"),
}
