package com.example.todoapp.domain.usecase

import androidx.paging.PagingData
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetTopHeadlinesUseCase(
    private val repository: NewsRepository,
) {
    operator fun invoke(category: String): Flow<PagingData<NewsArticle>> = repository.getNewsStream(category)
}
