package com.example.todoapp.domain.manager

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.model.MediaItem.Companion.toExoMediaItem
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@UnstableApi
class ExoPlayerManager(
    private val context: Context,
) {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentPlaylist: List<MediaItem> = emptyList()
    private var currentIndex = -1

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer
            .Builder(context)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .apply {
                addListener(playerListener)
                playWhenReady = true
            }
    }

    fun play(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem.toExoMediaItem())
        exoPlayer.prepare()

        _playerState.value =
            _playerState.value.copy(
                currentMediaItem = mediaItem,
                isPlaying = true,
            )
    }

    fun playPlaylist(
        playlist: List<MediaItem>,
        startIndex: Int = 0,
    ) {
        val mediaItems = playlist.map { it.toExoMediaItem() }
        currentPlaylist = playlist
        currentIndex = startIndex
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
        exoPlayer.seekTo(startIndex, 0L)
        exoPlayer.play()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.value = _playerState.value.copy(isPlaying = false)
        } else {
            exoPlayer.play()
            _playerState.value = _playerState.value.copy(isPlaying = true)
        }
    }

    fun seekTo(position: Long) {
        val safePosition = position.coerceIn(0, exoPlayer.duration)
        exoPlayer.seekTo(position.coerceIn(0, exoPlayer.duration))
        _playerState.value = _playerState.value.copy(currentPosition = safePosition)
    }

    fun next() {
        exoPlayer.seekToNextMediaItem()
        updateCurrentMediaItem()
    }

    fun previous() {
        exoPlayer.seekToPreviousMediaItem()
        updateCurrentMediaItem()
    }

    fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    fun setShuffleEnabled(enabled: Boolean) {
        exoPlayer.shuffleModeEnabled = enabled
    }

    fun getCurrentPosition() = exoPlayer.currentPosition

    fun getDuration() = exoPlayer.duration

    fun release() {
        exoPlayer.release()
    }

    private fun updateCurrentMediaItem() {
        currentIndex = exoPlayer.currentMediaItemIndex
        if (currentIndex in currentPlaylist.indices) {
            _playerState.value =
                _playerState.value.copy(
                    currentMediaItem = currentPlaylist.getOrNull(currentIndex),
                )
        }
    }

    private val playerListener =
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _playbackState.value =
                    when (playbackState) {
                        Player.STATE_IDLE -> PlaybackState.IDLE
                        Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                        Player.STATE_READY -> PlaybackState.READY
                        Player.STATE_ENDED -> PlaybackState.ENDED
                        else -> PlaybackState.IDLE
                    }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                updateCurrentMediaItem()
            }

            override fun onEvents(
                player: Player,
                events: Player.Events,
            ) {
                _playerState.value =
                    _playerState.value.copy(
                        currentPosition = player.currentPosition,
                        duration = player.duration,
                        bufferedPercentage = player.bufferedPercentage,
                    )
            }
        }
}
