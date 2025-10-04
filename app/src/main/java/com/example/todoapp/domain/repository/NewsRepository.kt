package com.example.todoapp.domain.repository

import androidx.paging.PagingData
import com.example.todoapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(): Flow<PagingData<NewsArticle>>

    fun getMockHeadlines(): Flow<PagingData<NewsArticle>>
}
