package com.example.todoapp.presentation.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.usecase.GetTopHeadlinesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewsViewModel(
    getTopUseCase: GetTopHeadlinesUseCase,
) : ViewModel() {
    val news: Flow<PagingData<NewsArticle>> = getTopUseCase().cachedIn(viewModelScope)

    private val _scrollState = mutableStateOf<LazyListState?>(null)
    val scrollState: State<LazyListState?> get() = _scrollState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun saveScrollState(state: LazyListState) {
        _scrollState.value = state
    }

    fun refresh() {
        _isRefreshing.value = false
    }

    fun getNewsById(id: Int): Flow<NewsArticle?> = snapshotFlow { null }
}
