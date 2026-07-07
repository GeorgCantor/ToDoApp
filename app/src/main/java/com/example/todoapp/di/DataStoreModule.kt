package com.example.todoapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.example.todoapp.data.datastore.NetworkMetricsSerializer
import com.example.todoapp.data.datastore.UserProfileSerializer
import com.example.todoapp.domain.model.NetworkMetricsList
import com.example.todoapp.domain.model.UserProfile
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USER_PROFILE_DATA_STORE_FILE_NAME = "user_profile.pb"
private const val NETWORK_METRICS_DATA_STORE_FILE_NAME = "network_metrics.json"

val Context.userProfileDataStore: DataStore<UserProfile> by dataStore(
    fileName = USER_PROFILE_DATA_STORE_FILE_NAME,
    serializer = UserProfileSerializer,
    corruptionHandler =
        ReplaceFileCorruptionHandler {
            UserProfile()
        },
)

val Context.networkMetricsDataStore: DataStore<NetworkMetricsList> by dataStore(
    fileName = NETWORK_METRICS_DATA_STORE_FILE_NAME,
    serializer = NetworkMetricsSerializer,
    corruptionHandler =
        ReplaceFileCorruptionHandler {
            NetworkMetricsList()
        },
)

val dataStoreModule =
    module {
        single<DataStore<UserProfile>> {
            androidContext().userProfileDataStore
        }
        single<DataStore<NetworkMetricsList>> {
            androidContext().networkMetricsDataStore
        }
    }
