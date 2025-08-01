package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@Composable
fun SearchNewsScreen(
    navController: NavController,
    viewModel: NewsViewModel,
) {
    var query by remember { mutableStateOf("") }
    val filteredNews =
        viewModel.news.filter {
            it.title.contains(query, ignoreCase = true) || it.description?.contains(query, ignoreCase = true) == true
        }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search news") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        )

        LazyColumn {
            items(filteredNews) { article ->
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
