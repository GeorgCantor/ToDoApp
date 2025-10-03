package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchNewsScreen(
    navController: NavController,
    viewModel: NewsViewModel,
) {
    var query by remember { mutableStateOf("") }
    val newsPagingItems = viewModel.news.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    val filteredItems =
        remember(newsPagingItems.itemSnapshotList.items, query) {
            if (query.isBlank()) {
                newsPagingItems.itemSnapshotList.items
            } else {
                newsPagingItems.itemSnapshotList.items.filter { article ->
                    article.title.contains(query, ignoreCase = true) ||
                        article.description?.contains(query, ignoreCase = true) == true
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search News") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search news...") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                items(
                    items = filteredItems,
                    key = { it.id },
                ) { article ->
                    NewsArticleItem(
                        article = article,
                        onClick = {
                            navController.navigate(NavRoutes.NewsDetail.createRoute(article.id))
                        },
                    )
                }
            }
        }
    }
}
