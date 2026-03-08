package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.repository.LocationRepository

class RequestExactLocationUseCase(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke() =
        if (repository.isLocationEnabled()) {
            repository.getExactLocation()
        } else {
            LocationResult.Error("Геолокация выключена")
        }
}
