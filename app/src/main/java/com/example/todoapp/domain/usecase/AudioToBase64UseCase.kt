package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository
import java.io.File

class AudioToBase64UseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(file: File) = repository.audioToBase64(file)
}
