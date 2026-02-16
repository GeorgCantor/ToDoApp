package com.example.todoapp.domain.model

import com.google.android.gms.maps.LocationSource
import java.util.UUID

sealed class LocationResult {
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val source: LocationSource,
        val accuracy: Float? = null,
        val timestamp: Long = System.currentTimeMillis(),
    ) : LocationResult()

    data object NotAvailable : LocationResult()

    data class Error(
        val reason: String,
    ) : LocationResult()
}

enum class LocationSource { IP_API, CACHE, GPS }

data class LocationPoint(
    val id: String = UUID.randomUUID().toString(),
    val latitude: Double,
    val longitude: Double,
    val source: LocationSource,
    val accuracy: Float? = null,
    val timestamp: Long,
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val source: LocationSource,
    val timestamp: Long,
)
