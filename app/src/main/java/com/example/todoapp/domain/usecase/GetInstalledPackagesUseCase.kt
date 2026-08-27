package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.PackageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class GetInstalledPackagesUseCase(
    private val repository: PackageRepository,
) {
    suspend operator fun invoke() = repository.getInstalledPackages()

    fun observePackages() =
        flow {
            while (true) {
                emit(repository.getInstalledPackages())
                delay(30_000)
            }
        }
}
