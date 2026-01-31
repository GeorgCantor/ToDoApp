package com.example.todoapp.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.todoapp.R
import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import com.example.todoapp.domain.model.PlayerUiState
import com.example.todoapp.presentation.viewmodel.PlayerViewModel
import com.example.todoapp.utils.formatTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showPlaylistSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            PlaylistBottomSheet(
                currentPlaylist = playerState.currentMediaItem?.let { listOf(it) }.orEmpty(),
                onClose = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
            )
        },
        sheetPeekHeight = 80.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShadowElevation = 16.dp,
    ) { paddingValues ->
        Scaffold(
            modifier = Modifier.padding(paddingValues),
            topBar = {
                PlayerTopAppBar(
                    showSearchBar = showSearchBar,
                    onSearchClicked = { showSearchBar = !showSearchBar },
                    onSearchQueryChanged = viewModel::search,
                    onClearSearch = viewModel::clearSearch,
                    searchQuery = searchQuery,
                )
            },
            floatingActionButton = {
                if (uiState.currentMediaItem != null) {
                    FloatingActionButton(
                        onClick = { showPlaylistSheet = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add to playlist")
                    }
                }
            },
        ) { innerPadding ->
            PlayerContent(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                mediaItems = mediaItems,
                isLoading = isLoading,
                playerState = playerState,
                playbackState = playbackState,
                onPlayMedia = viewModel::playMedia,
                onPlayPause = viewModel::togglePlayPause,
                onNext = viewModel::next,
                onPrevious = viewModel::previous,
                onSeekTo = viewModel::seekTo,
                onDeleteMedia = viewModel::deleteMediaItem,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTopAppBar(
    showSearchBar: Boolean,
    onSearchClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    searchQuery: String,
) {
    if (showSearchBar) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search media...") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onSearchClicked) {
                    Icon(Icons.Default.Close, contentDescription = "Close search")
                }
            }
        }
    } else {
        TopAppBar(
            title = { Text("Music Player", fontWeight = FontWeight.Bold) },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            actions = {
                IconButton(onClick = onSearchClicked) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
        )
    }
}

@Composable
fun PlayerContent(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    mediaItems: List<MediaItem>,
    isLoading: Boolean,
    playerState: PlayerState,
    playbackState: PlaybackState,
    onPlayMedia: (MediaItem) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onDeleteMedia: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CurrentPlayerSection(
            uiState = uiState,
            playerState = playerState,
            playbackState = playbackState,
            onPlayPause = onPlayPause,
            onNext = onNext,
            onPrevious = onPrevious,
            onSeekTo = onSeekTo,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        MediaListSection(
            mediaItems = mediaItems,
            isLoading = isLoading,
            currentMediaItem = playerState.currentMediaItem,
            onPlayMedia = onPlayMedia,
            onDeleteMedia = onDeleteMedia,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        )
    }
}

@Composable
fun CurrentPlayerSection(
    uiState: PlayerUiState,
    playerState: PlayerState,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentMedia = uiState.currentMediaItem

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (currentMedia != null) {
            MediaArtwork(
                mediaItem = currentMedia,
                modifier =
                    Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp)),
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = currentMedia.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = currentMedia.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            PlayerProgressBar(
                currentPosition = playerState.currentPosition,
                duration = playerState.duration,
                bufferedPercentage = playerState.bufferedPercentage,
                onSeekTo = onSeekTo,
                modifier = Modifier.fillMaxWidth(),
            )

            PlayerControls(
                isPlaying = playerState.isPlaying,
                playbackState = playbackState,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.widget_preview),
                    contentDescription = "No music",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No Media Playing",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Select a song to start playing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun MediaArtwork(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (mediaItem.artworkUri != null) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(mediaItem.artworkUri)
                            .crossfade(true)
                            .build(),
                    contentDescription = "Album art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.widget_preview),
                    contentDescription = "No album art",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f),
                                        ),
                                ),
                        ),
            )
        }
    }
}

@Composable
fun PlayerProgressBar(
    currentPosition: Long,
    duration: Long,
    bufferedPercentage: Int,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (bufferedPercentage > 0) {
            LinearProgressIndicator(
                progress = bufferedPercentage / 100f,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { newValue ->
                onSeekTo(newValue.coerceAtLeast(0F).toLong())
            },
            valueRange = 0f..maxOf(duration, 0).toFloat(),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = currentPosition.formatTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = duration.formatTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var repeatMode by remember { mutableIntStateOf(0) }
        IconButton(
            onClick = { repeatMode = (repeatMode + 1) % 3 },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector =
                    when (repeatMode) {
                        1 -> Icons.Outlined.KeyboardArrowDown
                        else -> Icons.Outlined.KeyboardArrowUp
                    },
                contentDescription = "Repeat",
                tint = if (repeatMode > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(56.dp),
        ) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier.size(40.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center,
        ) {
            when (playbackState) {
                PlaybackState.BUFFERING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp,
                    )
                }

                else -> {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Menu else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(56.dp),
        ) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier.size(40.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        var isShuffled by remember { mutableStateOf(false) }
        IconButton(
            onClick = { isShuffled = !isShuffled },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                Icons.Default.List,
                contentDescription = "Shuffle",
                tint = if (isShuffled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun MediaListSection(
    mediaItems: List<MediaItem>,
    isLoading: Boolean,
    currentMediaItem: MediaItem?,
    onPlayMedia: (MediaItem) -> Unit,
    onDeleteMedia: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Media Library",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (mediaItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.widget_preview),
                        contentDescription = "No media",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No media found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(
                    items = mediaItems,
                    key = { _, item -> item.id },
                ) { _, mediaItem ->
                    MediaItemCard(
                        mediaItem = mediaItem,
                        isPlaying = currentMediaItem?.id == mediaItem.id,
                        onClick = { onPlayMedia(mediaItem) },
                        onDelete = { onDeleteMedia(mediaItem.id) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(72.dp)
                .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        colors =
            if (isPlaying) {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            } else {
                CardDefaults.cardColors()
            },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                val density = LocalDensity.current
                if (mediaItem.artworkUri != null) {
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(mediaItem.artworkUri)
                                .crossfade(true)
                                .size(with(density) { 48.dp.roundToPx() })
                                .build(),
                        contentDescription = "Album art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.widget_preview),
                        contentDescription = "No album art",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = mediaItem.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = mediaItem.duration.formatTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp),
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistBottomSheet(
    currentPlaylist: List<MediaItem>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Current Playlist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentPlaylist.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.widget_preview),
                    contentDescription = "Empty playlist",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Playlist is empty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = currentPlaylist, key = { it.id }) { mediaItem ->
                    ListItem(
                        headlineContent = { Text(mediaItem.title) },
                        supportingContent = { Text(mediaItem.artist ?: "Unknown artist") },
                        leadingContent = {
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (mediaItem.artworkUri != null) {
                                    AsyncImage(
                                        model = mediaItem.artworkUri,
                                        contentDescription = "Album art",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.widget_preview),
                                        contentDescription = "No album art",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
