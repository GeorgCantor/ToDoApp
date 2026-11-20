package com.example.todoapp.data.model

import android.graphics.Bitmap

data class PackageInfoEntity(
    val packageName: String,
    val appName: String,
    val icon: Bitmap?,
    val versionName: String,
    val versionCode: Long,
    val installTime: Long,
    val updateTime: Long,
    val signatureType: String,
    val isSystemApp: Boolean,
    val isEnabled: Boolean,
    val targetSdk: Int,
    val minSdk: Int,
)
