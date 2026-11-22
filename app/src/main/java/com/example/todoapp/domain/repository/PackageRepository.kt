package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.PackageInfo

interface PackageRepository {
    suspend fun getInstalledPackages(): List<PackageInfo>

    suspend fun getPackageDetail(packageName: String): PackageInfo?
}
