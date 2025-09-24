package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.DocumentRepository
import java.io.File

class DownloadDocumentUseCase(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(downloadUrl: String): File {
        return repository.downloadDummyDocument(downloadUrl)
    }
}