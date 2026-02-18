package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.LocationRepository

class GetLocationHistoryUseCase(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(limit: Int = 100) = repository.getLocationHistory(limit)
}
