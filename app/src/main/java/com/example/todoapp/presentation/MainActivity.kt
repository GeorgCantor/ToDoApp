package com.example.todoapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ApiClient
import com.example.todoapp.data.repository.ChatRepositoryImpl
import com.example.todoapp.data.repository.NewsRepositoryImpl
import com.example.todoapp.domain.usecase.GetChatMessagesUseCase
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.domain.usecase.SendMessageUseCase
import com.example.todoapp.presentation.navigation.MainNavigation
import com.example.todoapp.presentation.theme.YourAppTheme
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels {
        NewsViewModelFactory(GetTopHeadlinesUseCase(NewsRepositoryImpl(ApiClient.newsApiService)))
    }
    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            SendMessageUseCase(ChatRepositoryImpl()),
            GetChatMessagesUseCase(ChatRepositoryImpl()),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YourAppTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    MainNavigation(viewModel = viewModel, chatViewModel = chatViewModel)
                }
            }
        }
    }
}

class NewsViewModelFactory(
    private val getTopUseCase: GetTopHeadlinesUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(getTopUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ChatViewModelFactory(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetChatMessagesUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(sendMessageUseCase, getMessagesUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
