package com.example.todoapp.domain.model

data class PlayerUiState(
    val currentMediaItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val bufferedPercentage: Int = 0,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val recentMedia: List<MediaItem> = emptyList(),
    val error: String? = null,
)
