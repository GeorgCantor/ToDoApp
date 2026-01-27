package com.example.todoapp.domain.model

data class PlayerState(
    val currentMediaItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val bufferedPercentage: Int = 0,
    val error: Throwable? = null,
)

enum class PlaybackState { IDLE, BUFFERING, READY, ENDED }
