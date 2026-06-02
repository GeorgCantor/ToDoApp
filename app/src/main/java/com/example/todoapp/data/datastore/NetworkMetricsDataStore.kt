package com.example.todoapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.example.todoapp.domain.model.NetworkMetricsList

private const val NETWORK_METRICS_DATA_STORE_FILE_NAME = "network_metrics.json"

val Context.networkMetricsDataStore: DataStore<NetworkMetricsList> by dataStore(
    fileName = NETWORK_METRICS_DATA_STORE_FILE_NAME,
    serializer = NetworkMetricsSerializer,
    corruptionHandler =
        ReplaceFileCorruptionHandler {
            NetworkMetricsList()
        },
)
