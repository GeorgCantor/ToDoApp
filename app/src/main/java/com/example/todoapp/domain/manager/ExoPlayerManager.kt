package com.example.todoapp.domain.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.todoapp.domain.model.MediaItem.Companion.toExoMediaItem
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.todoapp.domain.model.MediaItem as AppMediaItem

@UnstableApi
class ExoPlayerManager(
    private val context: Context,
) {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentPlaylist: List<AppMediaItem> = emptyList()
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

    fun getPlayer() = exoPlayer

    fun play(mediaItem: AppMediaItem) {
        exoPlayer.setMediaItem(mediaItem.toExoMediaItem())
        exoPlayer.prepare()
    }

    fun playPlaylist(
        playlist: List<AppMediaItem>,
        startIndex: Int = 0,
    ) {
        val mediaItems = playlist.map { it.toExoMediaItem() }
        currentPlaylist = playlist
        currentIndex = startIndex.coerceIn(0, playlist.size - 1)
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
        exoPlayer.seekTo(startIndex, 0L)
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(position: Long) {
        val safePosition = position.coerceIn(0, exoPlayer.duration)
        exoPlayer.seekTo(safePosition)
        _playerState.value = _playerState.value.copy(currentPosition = safePosition)
    }

    fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            exoPlayer.seekTo(0)
        }
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
        _playerState.value = PlayerState()
        _playbackState.value = PlaybackState.IDLE
    }

    fun setupAudioFocus() {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioFocusRequest =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioFocusRequest
                    .Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    ).setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { focusChange ->
                        when (focusChange) {
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                exoPlayer.play()
                                exoPlayer.volume = 1.0F
                            }

                            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                exoPlayer.pause()
                                _playerState.value = _playerState.value.copy(isPlaying = false)
                            }

                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> exoPlayer.volume = 0.2F
                        }
                    }.build()
            } else {
                null
            }

        val result =
            if (audioFocusRequest != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.requestAudioFocus(audioFocusRequest)
            } else {
                manager.requestAudioFocus(
                    { focusChange ->
                        when (focusChange) {
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                exoPlayer.play()
                                exoPlayer.volume = 1.0F
                            }

                            AudioManager.AUDIOFOCUS_LOSS -> {
                                exoPlayer.pause()
                                _playerState.value = _playerState.value.copy(isPlaying = false)
                            }

                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                exoPlayer.pause()
                                _playerState.value = _playerState.value.copy(isPlaying = false)
                            }

                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                                exoPlayer.volume = 0.2F
                            }
                        }
                    },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN,
                )
            }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // TODO
        }
    }

    fun releaseAudioFocus() {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.abandonAudioFocusRequest(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build(),
            )
        } else {
            manager.abandonAudioFocus(null)
        }
    }

    private fun updateCurrentMediaItem() {
        currentIndex = exoPlayer.currentMediaItemIndex
        if (currentIndex in currentPlaylist.indices) {
            val newMediaItem = currentPlaylist[currentIndex]
            _playerState.value =
                _playerState.value.copy(
                    currentMediaItem = newMediaItem,
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

                if (playbackState == Player.STATE_ENDED) {
                    _playerState.value =
                        _playerState.value.copy(
                            isPlaying = false,
                            currentPosition = exoPlayer.duration,
                        )
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
                if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION ||
                    reason == Player.DISCONTINUITY_REASON_SEEK ||
                    reason == Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT
                ) {
                    updateCurrentMediaItem()
                }
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int,
            ) {
                when (reason) {
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO,
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK,
                    -> {
                        updateCurrentMediaItem()
                    }
                }
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
