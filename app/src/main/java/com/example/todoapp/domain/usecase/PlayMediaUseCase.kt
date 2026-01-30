package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.manager.ExoPlayerManager
import com.example.todoapp.domain.model.MediaItem

class PlayMediaUseCase(
    private val manager: ExoPlayerManager,
) {
    operator fun invoke(mediaItem: MediaItem) {
        manager.play(mediaItem)
    }

    operator fun invoke(
        mediaItems: List<MediaItem>,
        startIndex: Int = 0,
    ) {
        manager.playPlaylist(mediaItems, startIndex)
    }
}
