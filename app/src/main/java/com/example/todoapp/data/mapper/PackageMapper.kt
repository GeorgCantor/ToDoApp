package com.example.todoapp.data.mapper

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import com.example.todoapp.data.model.PackageInfoEntity
import com.example.todoapp.domain.model.PackageInfo
import com.example.todoapp.domain.model.SignatureType
import android.content.pm.PackageInfo as AndroidPackageInfo

class PackageMapper(
    private val packageManager: PackageManager,
) {
    fun toDomain(entity: PackageInfoEntity) =
        PackageInfo(
            packageName = entity.packageName,
            appName = entity.appName,
            icon = entity.icon,
            versionName = entity.versionName,
            versionCode = entity.versionCode,
            installTime = entity.installTime,
            updateTime = entity.updateTime,
            signatureType = runCatching { SignatureType.valueOf(entity.signatureType) }.getOrDefault(SignatureType.UNKNOWN),
            isSystemApp = entity.isSystemApp,
            isEnabled = entity.isEnabled,
            targetSdk = entity.targetSdk,
            minSdk = entity.minSdk,
        )

    fun fromAndroidPackageInfo(
        packageInfo: AndroidPackageInfo,
        signatureType: SignatureType,
    ): PackageInfoEntity {
        val appInfo = packageInfo.applicationInfo
        return PackageInfoEntity(
            packageName = packageInfo.packageName,
            appName = appInfo?.let { packageManager.getApplicationLabel(it).toString() } ?: packageInfo.packageName,
            icon = appInfo?.let { loadIcon(it) },
            versionName = packageInfo.versionName ?: "Unknown",
            versionCode =
                if (SDK_INT >= P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                },
            installTime = packageInfo.firstInstallTime,
            updateTime = packageInfo.lastUpdateTime,
            signatureType = signatureType.name,
            isSystemApp = appInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0,
            isEnabled = appInfo?.enabled ?: true,
            targetSdk = appInfo?.targetSdkVersion ?: 0,
            minSdk = appInfo?.minSdkVersion ?: 0,
        )
    }

    private fun loadIcon(info: ApplicationInfo): Bitmap? =
        runCatching {
            (packageManager.getApplicationIcon(info) as? BitmapDrawable)?.bitmap
        }.getOrNull()
}
