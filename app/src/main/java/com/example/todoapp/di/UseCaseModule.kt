package com.example.todoapp.di

import com.example.todoapp.domain.repository.ChatRepository
import com.example.todoapp.domain.repository.NewsRepository
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetTopHeadlinesUseCase(repository: NewsRepository): GetTopHeadlinesUseCase {
        return GetTopHeadlinesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSendMessageUseCase(repository: ChatRepository): SendMessageUseCase {
        return SendMessageUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetChatMessagesUseCase(repository: ChatRepository): GetChatMessagesUseCase {
        return GetChatMessagesUseCase(repository)
    }
}