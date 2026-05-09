package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository

class RemoveReactionUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        messageId: String,
        reaction: String,
        userId: String,
    ) = repository.removeReaction(messageId, reaction, userId)
}
