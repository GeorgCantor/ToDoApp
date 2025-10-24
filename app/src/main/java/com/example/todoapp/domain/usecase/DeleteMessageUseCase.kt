package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository

class DeleteMessageUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(messageId: String) = repository.deleteMessage(messageId)
}
