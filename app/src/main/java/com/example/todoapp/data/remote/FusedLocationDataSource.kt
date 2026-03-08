package com.example.todoapp.data.remote

import android.location.Location
import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.model.LocationSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface FusedLocationDataSource {
    suspend fun getLastKnownLocation(): LocationResult

    suspend fun getCurrentLocation(): LocationResult
}

class FusedLocationDataSourceImpl(
    private val locationClient: FusedLocationProviderClient,
) : FusedLocationDataSource {
    override suspend fun getLastKnownLocation() =
        suspendCancellableCoroutine {
            try {
                locationClient.lastLocation
                    .addOnSuccessListener { location ->
                        it.resume(location.toLocationResult())
                    }.addOnFailureListener { e ->
                        it.resume(LocationResult.Error(e.message ?: "Failed to get location"))
                    }
            } catch (e: SecurityException) {
                it.resume(LocationResult.Error("Location permission denied"))
            }
        }

    override suspend fun getCurrentLocation() =
        suspendCancellableCoroutine {
            try {
                locationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener) = this

                            override fun isCancellationRequested() = false
                        },
                    ).addOnSuccessListener { location ->
                        it.resume(location.toLocationResult())
                    }.addOnFailureListener { e ->
                        it.resume(LocationResult.Error(e.message ?: "Failed to get location"))
                    }
            } catch (e: SecurityException) {
                it.resume(LocationResult.Error("Location permission denied"))
            }
        }
}

private fun Location?.toLocationResult() =
    if (this != null) {
        LocationResult.Success(
            latitude = latitude,
            longitude = longitude,
            source = LocationSource.GPS,
            accuracy = accuracy,
        )
    } else {
        LocationResult.NotAvailable
    }
