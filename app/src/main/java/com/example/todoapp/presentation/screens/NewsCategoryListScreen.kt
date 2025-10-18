package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.todoapp.domain.model.NewsCategory
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@Composable
fun NewsCategoryListScreen(
    category: NewsCategory,
    viewModel: NewsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(category) {
        viewModel.setCategory(category.apiValue)
    }

    val newsPagingItems = viewModel.news.collectAsLazyPagingItems()

    val listState = rememberLazyListState()

    LaunchedEffect(category) {
        newsPagingItems.refresh()
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (newsPagingItems.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            NewsPagingList(
                newsPagingItems = newsPagingItems,
                listState = listState,
                onItemClick = { article ->
                    viewModel.saveScrollState(listState)
                    navController.navigate(NavRoutes.NewsDetail.createRoute(article.id))
                },
            )
        }

        val error =
            when {
                newsPagingItems.loadState.refresh is LoadState.Error ->
                    newsPagingItems.loadState.refresh as LoadState.Error

                newsPagingItems.loadState.append is LoadState.Error ->
                    newsPagingItems.loadState.append as LoadState.Error

                else -> null
            }

        error?.let {
            Text(
                text = "Ошибка загрузки ${category.displayName}: ${it.error.message.orEmpty()}",
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
            )
        }
    }
}
