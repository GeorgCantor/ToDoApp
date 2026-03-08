package com.example.todoapp.data.remote

import com.example.todoapp.domain.model.LocationResult
import com.example.todoapp.domain.model.LocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

interface IpLocationDataSource {
    suspend fun getLocation(): LocationResult
}

class IpLocationDataSourceImpl(
    private val client: OkHttpClient,
) : IpLocationDataSource {
    override suspend fun getLocation() =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url("http://ip-api.com/json/").build()
                client.newCall(request).execute().use {
                    if (!it.isSuccessful) return@use LocationResult.NotAvailable
                    val json = JSONObject(it.body?.string() ?: return@use LocationResult.NotAvailable)
                    if (json.getString("status") == "success") {
                        LocationResult.Success(
                            latitude = json.getDouble("lat"),
                            longitude = json.getDouble("lon"),
                            source = LocationSource.IP_API,
                        )
                    } else {
                        LocationResult.NotAvailable
                    }
                }
            } catch (e: Exception) {
                LocationResult.Error(e.message ?: "IP location failed")
            }
        }
}
