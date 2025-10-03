package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import com.example.todoapp.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    navController: NavController,
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier,
) {
    val newsPagingItems = viewModel.news.collectAsLazyPagingItems()
    val savedScrollState = viewModel.scrollState.value
    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = savedScrollState?.firstVisibleItemIndex ?: 0,
            initialFirstVisibleItemScrollOffset = savedScrollState?.firstVisibleItemScrollOffset ?: 0,
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News") },
                actions = {
                    IconButton(
                        onClick = { newsPagingItems.refresh() },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                        )
                    }

                    IconButton(
                        onClick = { navController.navigate(NavRoutes.Search.route) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
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
                    text = "Ошибка: ${it.error.message}",
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                )
            }
        }
    }
}

@Composable
fun NewsPagingList(
    newsPagingItems: LazyPagingItems<NewsArticle>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onItemClick: (NewsArticle) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        items(
            count = newsPagingItems.itemCount,
            key = { index ->
                val article = newsPagingItems[index]
                article?.id ?: index
            },
        ) { index ->
            val newsArticle = newsPagingItems[index]

            if (newsArticle != null) {
                NewsArticleItem(
                    article = newsArticle,
                    onClick = { onItemClick(newsArticle) },
                )
            } else {
                NewsArticlePlaceholder()
            }
        }

        when {
            newsPagingItems.loadState.append is LoadState.Loading -> {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillParentMaxWidth()
                                .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            newsPagingItems.loadState.append is LoadState.Error -> {
                item {
                    val error = (newsPagingItems.loadState.append as LoadState.Error).error
                    ErrorItem(
                        message = error.message ?: "Ошибка загрузки",
                        onRetry = { newsPagingItems.retry() },
                    )
                }
            }
        }
    }
}

@Composable
fun NewsArticlePlaceholder() {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun NewsArticleItem(
    article: NewsArticle,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            article.urlToImage?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp),
            )

            article.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = article.publishedAt.toFormattedDate(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun ErrorItem(
    message: String,
    onRetry: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}
