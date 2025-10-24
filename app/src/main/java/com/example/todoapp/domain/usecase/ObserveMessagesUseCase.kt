package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.ChatMessage
import com.example.todoapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val repository: ChatRepository,
) {
    operator fun invoke(): Flow<List<ChatMessage>> = repository.observeMessages()
}
