package com.example.todoapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.example.todoapp.data.datastore.UserProfileSerializer
import com.example.todoapp.domain.model.UserProfile
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USER_PROFILE_DATA_STORE_FILE_NAME = "user_profile.pb"

val Context.userProfileDataStore: DataStore<UserProfile> by dataStore(
    fileName = USER_PROFILE_DATA_STORE_FILE_NAME,
    serializer = UserProfileSerializer,
    corruptionHandler =
        ReplaceFileCorruptionHandler {
            UserProfile()
        },
)

val dataStoreModule =
    module {
        single<DataStore<UserProfile>> {
            androidContext().userProfileDataStore
        }
    }
