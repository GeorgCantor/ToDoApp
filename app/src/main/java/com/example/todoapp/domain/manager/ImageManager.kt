package com.example.todoapp.domain.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private const val PROFILE_IMAGES_DIR = "profile_images"

class ImageManager(
    private val context: Context,
) {
    suspend fun saveImageFromUri(uri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    return@withContext saveBitmap(bitmap)
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private fun saveBitmap(bitmap: Bitmap): String {
        val imagesDir = File(context.filesDir, PROFILE_IMAGES_DIR)
        if (!imagesDir.exists()) imagesDir.mkdirs()

        val imageFile = File(imagesDir, "${UUID.randomUUID()}.jpg")
        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        return imageFile.absolutePath
    }

    suspend fun getImageFile(path: String): File? =
        withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) file else null
        }

    suspend fun deleteImage(path: String) =
        withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun getImageUri(path: String): Uri = Uri.fromFile(File(path))
}
