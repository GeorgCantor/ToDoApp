package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PlayerRepository

class GetMediaItemsUseCase(
    private val repository: PlayerRepository,
) {
    suspend operator fun invoke() = repository.getMediaItems()

    suspend operator fun invoke(query: String) =
        if (query.isNotBlank()) {
            repository.searchMediaItems(query)
        } else {
            repository.getMediaItems()
        }
}
