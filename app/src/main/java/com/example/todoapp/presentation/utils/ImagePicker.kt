package com.example.todoapp.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImagePickerResult(
    val openGallery: () -> Unit,
    val openCamera: () -> Unit,
    val tempFileUri: Uri? = null,
)

@Composable
fun rememberImagePicker(
    onImageSelected: (Uri) -> Unit,
    onError: (String) -> Unit,
): ImagePickerResult {
    val context = LocalContext.current
    var tempFileUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { it?.let { onImageSelected(it) } },
        )

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) tempFileUri?.let { onImageSelected(it) }
            },
        )

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    createTempFileUri(context, onError)?.let { uri ->
                        tempFileUri = uri
                        cameraLauncher.launch(uri)
                    }
                } else {
                    onError("Camera permission denied")
                }
            },
        )

    val openGallery: () -> Unit = {
        galleryLauncher.launch("image/*")
    }

    val openCamera: () -> Unit = {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                createTempFileUri(context, onError)?.let { uri ->
                    tempFileUri = uri
                    cameraLauncher.launch(uri)
                }
            }
            else -> permissionLauncher.launch(permission)
        }
    }

    return remember {
        ImagePickerResult(
            openGallery = openGallery,
            openCamera = openCamera,
            tempFileUri = tempFileUri,
        )
    }
}

private fun createTempFileUri(
    context: Context,
    onError: (String) -> Unit,
): Uri? =
    try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile,
        )
    } catch (e: Exception) {
        onError("Failed to create temp file: ${e.message}")
        null
    }
