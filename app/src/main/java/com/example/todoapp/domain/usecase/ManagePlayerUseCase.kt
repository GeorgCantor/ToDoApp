package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.manager.ExoPlayerManager
import com.example.todoapp.domain.model.PlaybackState
import com.example.todoapp.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

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
}
