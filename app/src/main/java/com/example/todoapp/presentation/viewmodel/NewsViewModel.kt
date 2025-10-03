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
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    getTopUseCase: GetTopHeadlinesUseCase,
) : ViewModel() {
    val news: Flow<PagingData<NewsArticle>> = getTopUseCase().cachedIn(viewModelScope)

    private val _scrollState = mutableStateOf<LazyListState?>(null)
    val scrollState: State<LazyListState?> get() = _scrollState

    fun saveScrollState(state: LazyListState) {
        _scrollState.value = state
    }
}
