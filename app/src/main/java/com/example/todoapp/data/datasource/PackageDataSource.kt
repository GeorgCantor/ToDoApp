package com.example.todoapp.data.datasource

import com.example.todoapp.data.model.PackageInfoEntity

interface PackageDataSource {
    suspend fun getPackages(): List<PackageInfoEntity>

    suspend fun getPackageDetail(packageName: String): PackageInfoEntity?
}
