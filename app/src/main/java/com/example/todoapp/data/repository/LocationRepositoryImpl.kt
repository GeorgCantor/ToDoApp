package com.example.todoapp.data.repository

import android.content.Context
import android.location.LocationManager
import com.example.todoapp.data.remote.FusedLocationDataSource
import com.example.todoapp.data.remote.HistoryDataSource
import com.example.todoapp.data.remote.IpLocationDataSource
import com.example.todoapp.data.remote.LocationCacheDataSource
import com.example.todoapp.domain.model.LocationData
import com.example.todoapp.domain.model.LocationPoint
import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.model.LocationSource
import com.example.todoapp.domain.model.toLocationData
import com.example.todoapp.domain.model.toLocationPoint
import com.example.todoapp.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CACHE_VALIDITY_HOURS = 24L

class LocationRepositoryImpl(
    private val context: Context,
    private val ipLocationDataSource: IpLocationDataSource,
    private val fusedLocationDataSource: FusedLocationDataSource,
    private val cacheDataSource: LocationCacheDataSource,
    private val historyDataSource: HistoryDataSource,
) : LocationRepository {
    override suspend fun getApproximateLocation(): LocationResult {
        val ipResult = ipLocationDataSource.getLocation()
        if (ipResult is LocationResult.Success) {
            cacheLocation(ipResult.toLocationData())
            addHistoryPoint(ipResult.toLocationPoint())
            return ipResult
        }

        cacheDataSource.getLastLocation()?.let {
            val hours = (System.currentTimeMillis() - it.timestamp) / (1000 * 60 * 60)
            if (hours < CACHE_VALIDITY_HOURS) {
                return LocationResult.Success(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    source = LocationSource.CACHE,
                    timestamp = it.timestamp,
                )
            }
        }

        return LocationResult.NotAvailable
    }

    override suspend fun getExactLocation(): LocationResult {
        if (!isLocationEnabled()) return LocationResult.Error("Location is disabled")

        val lastKnown = fusedLocationDataSource.getLastKnownLocation()
        if (lastKnown is LocationResult.Success) {
            cacheLocation(lastKnown.toLocationData())
            addHistoryPoint(lastKnown.toLocationPoint())
            return lastKnown
        }

        val current = fusedLocationDataSource.getCurrentLocation()
        if (current is LocationResult.Success) {
            cacheLocation(current.toLocationData())
            addHistoryPoint(current.toLocationPoint())
        }
        return current
    }

    override suspend fun isLocationEnabled(): Boolean =
        withContext(Dispatchers.IO) {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    override suspend fun cacheLocation(location: LocationData) = cacheDataSource.saveLocation(location)

    override suspend fun getCachedLocation() = cacheDataSource.getLastLocation()

    override suspend fun addHistoryPoint(point: LocationPoint) = historyDataSource.addPoint(point)

    override suspend fun getLocationHistory(limit: Int) = historyDataSource.getRecentPoints(limit)

    override suspend fun clearOldHistory(olderThen: Long) = historyDataSource.clearOldPoints(olderThen)
}
