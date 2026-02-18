package com.example.todoapp.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapp.domain.model.LocationData
import com.example.todoapp.domain.model.LocationSource
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("location_cache")

interface LocationCacheDataSource {
    suspend fun saveLocation(location: LocationData)

    suspend fun getLastLocation(): LocationData?

    suspend fun clearCache()
}

class LocationCacheDataSourceImpl(
    private val context: Context,
) : LocationCacheDataSource {
    private val latitudeKey = floatPreferencesKey("latitude")
    private val longitudeKey = floatPreferencesKey("longitude")
    private val sourceKey = stringPreferencesKey("source")
    private val timestampKey = longPreferencesKey("timestamp")

    private var inMemoryCache: LocationData? = null

    override suspend fun saveLocation(location: LocationData) {
        inMemoryCache = location
        context.dataStore.edit {
            it[latitudeKey] = location.latitude.toFloat()
            it[longitudeKey] = location.longitude.toFloat()
            it[sourceKey] = location.source.name
            it[timestampKey] = location.timestamp
        }
    }

    override suspend fun getLastLocation(): LocationData? {
        inMemoryCache?.let { return it }
        val prefs = context.dataStore.data.first()
        val latitude = prefs[latitudeKey]?.toDouble() ?: return null
        val longitude = prefs[longitudeKey]?.toDouble() ?: return null
        val source = prefs[sourceKey]?.let { LocationSource.valueOf(it) } ?: return null
        val timestamp = prefs[timestampKey] ?: return null

        return LocationData(latitude, longitude, source, timestamp).also { inMemoryCache = it }
    }

    override suspend fun clearCache() {
        inMemoryCache = null
        context.dataStore.edit { it.clear() }
    }
}
