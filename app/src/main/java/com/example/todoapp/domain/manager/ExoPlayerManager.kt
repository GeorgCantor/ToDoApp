package com.example.todoapp.domain.manager

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExoPlayerManager(
    private val context: Context,
) {
    private var exoPlayer: ExoPlayer? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val currentPlaylist: List<MediaItem> = emptyList()
    private var currentIndex = -1

    init {
        initializePlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        exoPlayer =
            ExoPlayer
                .Builder(context)
                .setSeekBackIncrementMs(10000)
                .setSeekForwardIncrementMs(10000)
                .build()
                .apply {
                }
    }

    fun play(mediaItem: com.example.todoapp.domain.model.MediaItem) {
        val exoMediaItem =
            MediaItem
                .fromUri(mediaItem.uri)
                .buildUpon()
                .setMediaMetadata(
                    MediaMetadata
                        .Builder()
                        .setTitle(mediaItem.title)
                        .setArtist(mediaItem.artist)
                        .setAlbumTitle(mediaItem.album)
                        .setArtworkUri(mediaItem.artworkUri?.toUri())
                        .build(),
                ).build()

        exoPlayer?.setMediaItem(exoMediaItem)
        exoPlayer?.prepare()

        _playerState.value =
            _playerState.value.copy(
                currentMediaItem = mediaItem,
                isPlaying = true,
            )
    }
}
