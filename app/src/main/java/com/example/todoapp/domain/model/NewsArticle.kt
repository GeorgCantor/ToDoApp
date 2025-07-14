package com.example.todoapp.domain.model

data class NewsArticle(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)