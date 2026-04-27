package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository

class AddReactionUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        messageId: String,
        reaction: String,
        userId: String,
    ) = repository.addReaction(messageId, reaction, userId)
}
