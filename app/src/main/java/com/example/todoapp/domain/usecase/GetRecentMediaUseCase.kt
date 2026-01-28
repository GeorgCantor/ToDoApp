package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PlayerRepository

class GetRecentMediaUseCase(
    private val repository: PlayerRepository,
) {
    operator fun invoke() = repository.observeRecentMedia()
}
