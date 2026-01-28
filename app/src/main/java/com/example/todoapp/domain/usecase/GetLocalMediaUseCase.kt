package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PlayerRepository

class GetLocalMediaUseCase(
    private val repository: PlayerRepository,
) {
    suspend operator fun invoke() = repository.getLocalMediaItems()
}
