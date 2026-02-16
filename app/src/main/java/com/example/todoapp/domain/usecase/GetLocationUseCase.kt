package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.repository.LocationRepository

class GetLocationUseCase(
    repository: LocationRepository,
) {
    suspend operator fun invoke(): LocationResult {
    }
}
