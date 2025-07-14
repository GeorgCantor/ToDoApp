package com.example.todoapp.data.repository

import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.repository.NewsRepository

class NewsRepositoryImpl(private val apiService: NewsApiService) : NewsRepository {
    override suspend fun getTopHeadlines(): List<NewsArticle> {
        return try {
            val response = apiService.getTopHeadlines()
            if (response.status == "ok") {
                response.articles.map { it.toNewsArticle() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}