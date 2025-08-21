package com.example.todoapp.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getTopUseCase: GetTopHeadlinesUseCase
) : ViewModel() {
    private val _news = mutableStateListOf<NewsArticle>()
    val news: List<NewsArticle> get() = _news

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _news.clear()
                _news.addAll(getTopUseCase())
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNewsById(id: Int): Flow<NewsArticle?> = snapshotFlow { _news.toList() }.map { it.find { it.id == id } }
}
