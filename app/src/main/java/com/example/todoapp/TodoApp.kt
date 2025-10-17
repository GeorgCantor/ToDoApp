package com.example.todoapp

import android.app.Application
import com.example.todoapp.data.cache.NewsCache
import com.example.todoapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TodoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TodoApp)
            modules(appModule)
        }

        NewsCache.init(this)
    }
}
