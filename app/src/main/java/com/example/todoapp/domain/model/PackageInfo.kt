package com.example.todoapp.domain.model

import android.graphics.Bitmap

data class PackageInfo(
    val packageName: String,
    val appName: String,
    val icon: Bitmap? = null,
    val versionName: String,
    val versionCode: Long,
    val installTime: Long,
    val updateTime: Long,
    val signatureType: SignatureType,
    val isSystemApp: Boolean,
    val isEnabled: Boolean,
    val targetSdk: Int,
    val minSdk: Int,
)
