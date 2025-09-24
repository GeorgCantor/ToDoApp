package com.example.todoapp.di

import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.data.repository.ChatRepositoryImpl
import com.example.todoapp.data.repository.DocumentRepositoryImpl
import com.example.todoapp.data.repository.NewsRepositoryImpl
import com.example.todoapp.domain.repository.ChatRepository
import com.example.todoapp.domain.repository.DocumentRepository
import com.example.todoapp.domain.repository.NewsRepository
import com.example.todoapp.domain.usecase.DownloadDocumentUseCase
import com.example.todoapp.domain.usecase.GetAvailableDocumentsUseCase
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.DocumentsViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule =
    module {
        single {
            val loggingInterceptor =
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

            val okHttpClient =
                OkHttpClient
                    .Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

            Retrofit
                .Builder()
                .baseUrl("https://newsapi.org/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single<NewsApiService> {
            get<Retrofit>().create(NewsApiService::class.java)
        }

        single<NewsRepository> { NewsRepositoryImpl(get()) }
        single<ChatRepository> { ChatRepositoryImpl() }
        single<DocumentRepository> { DocumentRepositoryImpl() }

        factory { GetTopHeadlinesUseCase(get()) }
        factory { SendMessageUseCase(get()) }
        factory { GetChatMessagesUseCase(get()) }
        factory { GetAvailableDocumentsUseCase(get()) }
        factory { DownloadDocumentUseCase(get()) }

        viewModel { NewsViewModel(get()) }
        viewModel { ChatViewModel(get(), get()) }
        viewModel { DocumentsViewModel(get(), get()) }
    }
