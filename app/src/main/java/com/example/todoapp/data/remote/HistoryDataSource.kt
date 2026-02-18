package com.example.todoapp.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapp.domain.model.LocationPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore("location_history")

interface HistoryDataSource {
    suspend fun addPoint(point: LocationPoint)

    suspend fun getRecentPoints(limit: Int): List<LocationPoint>

    suspend fun clearOldPoints(olderThan: Long)
}

class HistoryDataSourceImpl(
    private val context: Context,
    private val gson: Gson,
) : HistoryDataSource {
    private val historyKey = stringPreferencesKey("history")
    private val maxPoints = 50

    override suspend fun addPoint(point: LocationPoint) {
        context.historyDataStore.edit { prefs ->
            val points = prefs[historyKey]?.let { deserializePoints(it) }.orEmpty()
            val updated = (points + point).takeLast(maxPoints)
            prefs[historyKey] = serializePoints(updated)
        }
    }

    override suspend fun getRecentPoints(limit: Int): List<LocationPoint> {
        val prefs = context.historyDataStore.data.first()
        val json = prefs[historyKey] ?: return emptyList()
        return deserializePoints(json).takeLast(limit)
    }

    override suspend fun clearOldPoints(olderThan: Long) {
        context.historyDataStore.edit { prefs ->
            val json = prefs[historyKey] ?: return@edit
            val points = deserializePoints(json).toMutableSet()
            points.removeIf { it.timestamp < olderThan }

            if (points.isEmpty()) {
                prefs.remove(historyKey)
            } else {
                prefs[historyKey] = serializePoints(points.toList())
            }
        }
    }

    private fun serializePoints(points: List<LocationPoint>) = gson.toJson(points)

    private fun deserializePoints(json: String): List<LocationPoint> =
        try {
            val type = object : TypeToken<List<LocationPoint>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
}
