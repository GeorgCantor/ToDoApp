package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.PackageInfo
import com.example.todoapp.domain.model.SignatureType

class GetPackagesGroupedBySignatureUseCase(
    private val getPackagesUseCase: GetInstalledPackagesUseCase,
) {
    suspend operator fun invoke(): Map<SignatureType, List<PackageInfo>> {
        val packages = getPackagesUseCase()
        return packages.groupBy { it.signatureType }
    }
}
