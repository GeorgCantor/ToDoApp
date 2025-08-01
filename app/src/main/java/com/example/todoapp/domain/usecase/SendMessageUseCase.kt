package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(message: ChatMessage) = repository.sendMessage(message)
}
