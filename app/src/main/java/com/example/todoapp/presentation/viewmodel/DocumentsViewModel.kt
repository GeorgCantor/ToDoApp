package com.example.todoapp.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.DocumentItem
import com.example.todoapp.domain.usecase.DownloadDocumentUseCase
import com.example.todoapp.domain.usecase.GetAvailableDocumentsUseCase
import com.example.todoapp.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class DocumentsViewModel(
    private val getAvailableDocumentsUseCase: GetAvailableDocumentsUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
) : ViewModel() {
    private val _documents = MutableStateFlow<List<DocumentItem>>(emptyList())
    val documents: StateFlow<List<DocumentItem>> get() = _documents

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _downloadProgress = MutableStateFlow<Pair<String, Int>?>(null)
    val downloadProgress: StateFlow<Pair<String, Int>?> get() = _downloadProgress

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    init {
        loadAvailableDocuments()
    }

    private fun loadAvailableDocuments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _documents.value = getAvailableDocumentsUseCase()
            } catch (e: Exception) {
                _error.value = "Failed to load documents: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun downloadDocument(
        document: DocumentItem,
        context: Context,
    ) {
        viewModelScope.launch {
            try {
                _downloadProgress.value = document.name to 0
                for (progress in 0..100 step 10) {
                    _downloadProgress.value = document.name to progress
                    delay(200)
                }
                val downloadedFile = downloadDocumentUseCase(document.downloadUrl)
                _downloadProgress.value = document.name to 100
                context.showToast("${document.name} downloaded successfully!")
                openFile(context, downloadedFile)
            } catch (e: Exception) {
                _error.value = "Download failed: ${e.message}"
                context.showToast("Download failed: ${e.message}")
            } finally {
                delay(1000)
                _downloadProgress.value = null
            }
        }
    }

    private fun openFile(
        context: Context,
        file: File,
    ) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            val uri =
                androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file,
                )
            intent.setDataAndType(uri, getMimeType(file))
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.showToast("Cannot open file: ${e.message}")
        }
    }

    private fun getMimeType(file: File): String =
        when (file.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "*/*"
        }
}
