package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage)
    suspend fun getMessages(): List<ChatMessage>
    fun observeMessages(): Flow<List<ChatMessage>>
}