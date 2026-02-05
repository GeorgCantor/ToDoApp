package com.example.todoapp

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.media3.common.util.UnstableApi
import com.example.todoapp.data.cache.NewsCache
import com.example.todoapp.di.appModule
import com.example.todoapp.di.dataStoreModule
import com.example.todoapp.domain.manager.ExoPlayerManager
import com.example.todoapp.domain.manager.SessionTracker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@UnstableApi
class TodoApp : Application() {
    lateinit var exoPlayerManager: ExoPlayerManager
        private set

    override fun onCreate() {
        super.onCreate()
        setupStrictMode()

        startKoin {
            androidContext(this@TodoApp)
            modules(appModule, dataStoreModule)
        }

        NewsCache.init(this)
        SessionTracker(this)
        exoPlayerManager = ExoPlayerManager(applicationContext)
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build(),
            )

            val vmPolicyBuilder =
                StrictMode.VmPolicy
                    .Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .detectLeakedRegistrationObjects()
                    .detectFileUriExposure()
                    .penaltyLog()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vmPolicyBuilder.detectContentUriWithoutPermission()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                vmPolicyBuilder.detectNonSdkApiUsage()
            }

            StrictMode.setVmPolicy(vmPolicyBuilder.build())
        }
    }
}
