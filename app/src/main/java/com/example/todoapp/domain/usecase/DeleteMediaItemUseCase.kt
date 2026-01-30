package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PlayerRepository

class DeleteMediaItemUseCase(
    private val repository: PlayerRepository,
) {
    suspend operator fun invoke(id: String) = repository.deleteMediaItem(id)
}
