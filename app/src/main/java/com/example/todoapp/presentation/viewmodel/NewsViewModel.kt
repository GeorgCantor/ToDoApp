package com.example.todoapp.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import kotlinx.coroutines.launch

class NewsViewModel(private val getTopUseCase: GetTopHeadlinesUseCase) : ViewModel() {
    private val _news = mutableStateListOf<NewsArticle>()
    val news: List<NewsArticle> get() = _news

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _news.clear()
                _news.addAll(getTopUseCase())
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}