package com.example.todoapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ApiClient
import com.example.todoapp.data.repository.NewsRepositoryImpl
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.presentation.screens.NewsListScreen
import com.example.todoapp.presentation.theme.YourAppTheme
import com.example.todoapp.presentation.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(GetTopHeadlinesUseCase(NewsRepositoryImpl(ApiClient.newsApiService)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NewsListScreen(viewModel = viewModel)
                }
            }
        }
    }
}

class NewsViewModelFactory(private val getTopUseCase: GetTopHeadlinesUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(getTopUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}