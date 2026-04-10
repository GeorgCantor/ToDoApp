package com.example.todoapp.di

import android.content.ContentResolver
import com.example.todoapp.data.datasource.MessageDataSource
import com.example.todoapp.data.datasource.MessageDataSourceImpl
import com.example.todoapp.data.sync.P2PManager
import com.example.todoapp.data.sync.SyncManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val syncModule =
    module {
        single<MessageDataSource> { MessageDataSourceImpl(androidContext()) }
        single { P2PManager(androidContext()) }
        single { SyncManager(androidContext(), get()) }
        single<ContentResolver> { androidContext().contentResolver }
    }
