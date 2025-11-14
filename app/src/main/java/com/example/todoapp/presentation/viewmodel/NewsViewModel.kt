package com.example.todoapp.presentation.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import com.example.todoapp.domain.usecase.UpdateUserStatisticsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getTopUseCase: GetTopHeadlinesUseCase,
    private val updateUserStatisticsUseCase: UpdateUserStatisticsUseCase,
) : ViewModel() {
    private val _currentCategory = MutableStateFlow("general")

    @OptIn(ExperimentalCoroutinesApi::class)
    val news: Flow<PagingData<NewsArticle>> =
        _currentCategory
            .flatMapLatest { category ->
                getTopUseCase(category).cachedIn(viewModelScope)
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

    private val _scrollState = mutableStateOf<LazyListState?>(null)
    val scrollState: State<LazyListState?> get() = _scrollState

    fun saveScrollState(state: LazyListState) {
        _scrollState.value = state
        trackNewsRead()
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    private fun trackNewsRead() {
        viewModelScope.launch {
            updateUserStatisticsUseCase { stats ->
                stats.copy(
                    newsRead = stats.newsRead + 1,
                    lastActive = System.currentTimeMillis(),
                )
            }
        }
    }
}
