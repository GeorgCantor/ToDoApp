package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.LocationData
import com.example.todoapp.domain.model.LocationPoint
import com.example.todoapp.domain.model.LocationResult

interface LocationRepository {
    suspend fun getApproximateLocation(): LocationResult

    suspend fun getExactLocation(): LocationResult

    suspend fun isLocationEnabled(): Boolean

    suspend fun cacheLocation(location: LocationData)

    suspend fun getCachedLocation(): LocationData?

    suspend fun addHistoryPoint(point: LocationPoint)

    suspend fun getLocationHistory(limit: Int): List<LocationPoint>

    suspend fun clearOldHistory(olderThen: Long)
}
