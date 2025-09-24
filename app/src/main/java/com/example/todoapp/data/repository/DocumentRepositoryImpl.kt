package com.example.todoapp.data.repository

import com.example.todoapp.domain.model.DocumentItem
import com.example.todoapp.domain.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DocumentRepositoryImpl : DocumentRepository {
    private val client =
        OkHttpClient
            .Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    override suspend fun downloadDummyDocument(downloadUrl: String): File =
        withContext(Dispatchers.IO) {
            val request =
                Request
                    .Builder()
                    .url(downloadUrl)
                    .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Download failed: ${response.code}")
                }
                val fileName = "downloaded_file_${System.currentTimeMillis()}"
                val file = File.createTempFile(fileName, null)

                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file
            }
        }

    override suspend fun getAvailableDocuments(): List<DocumentItem> =
        listOf(
            DocumentItem(
                id = "1",
                name = "Sample PDF Document",
                type = "PDF",
                size = "1.2 MB",
                description = "Real PDF from the web",
                downloadUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            ),
            DocumentItem(
                id = "2",
                name = "Sample Word Document",
                type = "DOCX",
                size = "15 KB",
                description = "Real DOCX file",
                downloadUrl = "https://file-examples.com/storage/fe8c7eef0c63b5a6a9d4a86/2017/02/file_example_DOCX_500kB.docx",
            ),
            DocumentItem(
                id = "3",
                name = "Sample Excel File",
                type = "XLSX",
                size = "10 KB",
                description = "Real Excel spreadsheet",
                downloadUrl = "https://file-examples.com/storage/fe8c7eef0c63b5a6a9d4a86/2017/02/file_example_XLSX_10.xlsx",
            ),
            DocumentItem(
                id = "4",
                name = "Sample Text File",
                type = "TXT",
                size = "1 KB",
                description = "Simple text document",
                downloadUrl = "https://file-examples.com/storage/fe8c7eef0c63b5a6a9d4a86/2017/02/file_example_TXT_1kB.txt",
            ),
            DocumentItem(
                id = "5",
                name = "Sample Image",
                type = "JPG",
                size = "200 KB",
                description = "JPEG image file",
                downloadUrl = "https://picsum.photos/800/600",
            ),
        )
}
