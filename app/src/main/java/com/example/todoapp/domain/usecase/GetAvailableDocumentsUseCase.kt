package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.DocumentItem
import com.example.todoapp.domain.repository.DocumentRepository

class GetAvailableDocumentsUseCase(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(): List<DocumentItem> {
        return repository.getAvailableDocuments()
    }
}