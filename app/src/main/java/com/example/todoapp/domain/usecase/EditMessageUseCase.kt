package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository

class EditMessageUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        messageId: String,
        newText: String,
    ) = repository.editMessage(messageId, newText)
}
