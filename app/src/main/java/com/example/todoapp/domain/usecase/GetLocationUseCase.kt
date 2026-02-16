package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.model.LocationSource
import com.example.todoapp.domain.repository.LocationRepository

class GetLocationUseCase(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(
        forceExact: Boolean = false,
        maxCacheAgeHours: Long = 24,
    ) = if (forceExact) {
        if (repository.isLocationEnabled()) {
            repository.getExactLocation()
        } else {
            LocationResult.Error("Геолокация выключена")
        }
    } else {
        when (val approximate = repository.getApproximateLocation()) {
            is LocationResult.Success -> {
                if (approximate.source == LocationSource.CACHE) {
                    val age = System.currentTimeMillis() - approximate.timestamp
                    val hours = age / (1000 * 60 * 60)
                    if (hours <= maxCacheAgeHours) approximate else proceedToExact()
                } else {
                    approximate
                }
            }

            is LocationResult.NotAvailable -> proceedToExact()
            is LocationResult.Error -> approximate
        }
    }

    private suspend fun proceedToExact() =
        if (repository.isLocationEnabled()) {
            repository.getExactLocation()
        } else {
            LocationResult.NotAvailable
        }
}
