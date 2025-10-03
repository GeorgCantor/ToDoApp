package com.example.todoapp.presentation.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: Int,
    viewModel: NewsViewModel,
    navController: NavController,
) {
    val newsPagingItems = viewModel.news.collectAsLazyPagingItems()
    var newsItem by remember { mutableStateOf<NewsArticle?>(null) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showZoomedImage by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }
    var ttsInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(newsPagingItems.itemSnapshotList, newsId) {
        newsItem = newsPagingItems.itemSnapshotList.items.find { it.id == newsId }
    }

    DisposableEffect(Unit) {
        val textToSpeech =
            TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    ttsInitialized = true
                }
            }
        textToSpeech.language = Locale.US
        tts = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            tts = null
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = newsItem?.title.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSpeaking) {
                            tts?.stop()
                            isSpeaking = false
                        }
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    newsItem?.let { item ->
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    tts?.stop()
                                    isSpeaking = false
                                } else {
                                    val textToSpeak =
                                        buildString {
                                            append(item.title)
                                            if (!item.description.isNullOrEmpty()) {
                                                append(". ")
                                                append(item.description)
                                            }
                                        }
                                    if (textToSpeak.isNotBlank()) {
                                        tts?.speak(
                                            textToSpeak,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "news_${item.id}",
                                        )
                                        isSpeaking = true
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Clear else Icons.Default.PlayArrow,
                                contentDescription = if (isSpeaking) "Stop reading" else "Read aloud",
                            )
                        }
                    }

                    if (showZoomedImage) {
                        IconButton(onClick = { showZoomedImage = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        newsItem?.let { item ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
            ) {
                AsyncImage(
                    model = item.urlToImage,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { showZoomedImage = true },
                )
                Text(
                    text = item.title,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = item.description.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                )
            }

            if (showZoomedImage) {
                ZoomableImage(
                    imageUrl = item.urlToImage.orEmpty(),
                    onClose = { showZoomedImage = false },
                )
            }
        }
    }

    DisposableEffect(tts) {
        val listener =
            TextToSpeech.OnUtteranceCompletedListener { utteranceId ->
                if (utteranceId == "news_${newsItem?.id}") {
                    isSpeaking = false
                }
            }
        tts?.setOnUtteranceCompletedListener(listener)
        onDispose {
            tts?.setOnUtteranceCompletedListener(null)
        }
    }
}
