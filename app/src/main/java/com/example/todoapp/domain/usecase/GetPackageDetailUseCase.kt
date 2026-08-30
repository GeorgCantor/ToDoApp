package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PackageRepository

class GetPackageDetailUseCase(
    private val repository: PackageRepository,
) {
    suspend operator fun invoke(packageName: String) = repository.getPackageDetail(packageName)
}
