package com.example.todoapp.domain.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.model.MediaItem.Companion.toExoMediaItem
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import com.example.todoapp.service.PlayerService
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

    fun startForegroundService() {
        PlayerService.startService(context)
    }

    fun stopForegroundService() {
        PlayerService.stopService(context)
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

                            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> exoPlayer.pause()

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
                    { _ -> },
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
        manager.abandonAudioFocus(null)
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
