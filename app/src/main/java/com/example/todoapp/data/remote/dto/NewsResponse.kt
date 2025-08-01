package com.example.todoapp.data.remote.dto

import com.example.todoapp.domain.model.NewsArticle

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>,
)

data class ArticleDto(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
) {
    fun toNewsArticle() =
        NewsArticle(
            id = url.hashCode(),
            title = title,
            description = description,
            url = url,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
        )
}
