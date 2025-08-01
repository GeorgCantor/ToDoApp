package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.repository.NewsRepository

class GetTopHeadlinesUseCase(
    private val repository: NewsRepository,
) {
    suspend operator fun invoke(): List<NewsArticle> = repository.getMockHeadlines()
}
