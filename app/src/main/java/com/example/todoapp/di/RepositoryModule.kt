package com.example.todoapp.di

import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.data.repository.ChatRepositoryImpl
import com.example.todoapp.data.repository.NewsRepositoryImpl
import com.example.todoapp.domain.repository.ChatRepository
import com.example.todoapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(apiService: NewsApiService): NewsRepository {
        return NewsRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideChatRepository(): ChatRepository {
        return ChatRepositoryImpl()
    }
}