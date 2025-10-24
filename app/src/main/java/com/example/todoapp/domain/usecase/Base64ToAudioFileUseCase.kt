package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ChatRepository
import java.io.File

class Base64ToAudioFileUseCase(
    private val repository: ChatRepository,
) {
    operator fun invoke(
        base64: String,
        cacheDir: File,
    ) = repository.base64ToAudioFile(base64, cacheDir)
}
