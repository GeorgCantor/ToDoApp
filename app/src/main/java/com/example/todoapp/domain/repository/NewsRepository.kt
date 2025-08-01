package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.NewsArticle

interface NewsRepository {
    suspend fun getTopHeadlines(): List<NewsArticle>

    suspend fun getMockHeadlines(): List<NewsArticle>
}
