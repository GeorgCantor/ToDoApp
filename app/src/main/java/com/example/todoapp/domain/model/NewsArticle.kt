package com.example.todoapp.domain.model

data class NewsArticle(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)