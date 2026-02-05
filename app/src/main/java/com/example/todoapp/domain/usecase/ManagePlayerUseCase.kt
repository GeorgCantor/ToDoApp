package com.example.todoapp.domain.usecase

import androidx.media3.common.util.UnstableApi
import com.example.todoapp.domain.manager.ExoPlayerManager
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

@UnstableApi
class ManagePlayerUseCase(
    private val manager: ExoPlayerManager,
) {
    val playerState: StateFlow<PlayerState>
        get() = manager.playerState

    val playbackState: StateFlow<PlaybackState>
        get() = manager.playbackState

    fun playPause() = manager.playPause()

    fun next() = manager.next()

    fun previous() = manager.previous()

    fun seekTo(position: Long) = manager.seekTo(position)

    fun getCurrentPosition(): Long = manager.getCurrentPosition()

    fun getDuration(): Long = manager.getDuration()

    fun setupAudioFocus() = manager.setupAudioFocus()

    fun releaseAudioFocus() = manager.releaseAudioFocus()
}
