package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage)

    suspend fun getMessages(): List<ChatMessage>

    fun observeMessages(): Flow<List<ChatMessage>>

    suspend fun audioToBase64(file: File): Pair<String, Long>

    fun base64ToAudioFile(
        base64: String,
        cacheDir: File,
    ): File
}
