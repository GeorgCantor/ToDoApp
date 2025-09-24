package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.DocumentItem
import java.io.File

interface DocumentRepository {
    suspend fun downloadDummyDocument(downloadUrl: String): File
    suspend fun getAvailableDocuments(): List<DocumentItem>
}