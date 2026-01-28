package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.MediaItem
import com.example.todoapp.domain.repository.PlayerRepository

class SaveMediaItemUseCase(
    private val repository: PlayerRepository,
) {
    suspend operator fun invoke(mediaItem: MediaItem) = repository.saveMediaItem(mediaItem)
}
