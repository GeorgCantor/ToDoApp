package com.example.todoapp.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.StatFs
import android.util.Log
import com.example.todoapp.ISystemCallback
import com.example.todoapp.ISystemInfoService
import com.example.todoapp.StorageInfo
import com.example.todoapp.SystemInfo
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

class SystemInfoService : Service() {
    private val callbacks = CopyOnWriteArrayList<ISystemCallback>()
    private var isMonitoring = false

    private val batteryReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPercent = (level * 100 / scale.toFloat()).roundToInt()

                callbacks.forEach { callback ->
                    try {
                        callback.onBatteryChanged(batteryPercent)
                    } catch (e: Exception) {
                        Log.e("SystemInfoService", "Error in battery callback", e)
                    }
                }
            }
        }

    private val storageReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                if (Intent.ACTION_DEVICE_STORAGE_LOW == intent.action) {
                    callbacks.forEach { callback ->
                        try {
                            callback.onStorageLow()
                        } catch (e: Exception) {
                            Log.e("SystemInfoService", "Error in storage callback", e)
                        }
                    }
                }
            }
        }

    private val binder =
        object : ISystemInfoService.Stub() {
            override fun getSystemInfo(): SystemInfo =
                SystemInfo(
                    deviceName = Build.DEVICE,
                    androidVersion = Build.VERSION.RELEASE,
                    apiLevel = Build.VERSION.SDK_INT,
                    manufacturer = Build.MANUFACTURER,
                    model = Build.MODEL,
                    batteryLevel = getBatteryLevel(),
                    availableMemory = getAvailableMemory(),
                    totalMemory = getTotalMemory(),
                    isCharging = isCharging(),
                    timestamp = System.currentTimeMillis(),
                )

            override fun getStorageInfo(): StorageInfo {
                val internalStats = StatFs(File("/data").absolutePath)
                val externalStats = StatFs(System.getenv("EXTERNAL_STORAGE") ?: "/sdcard")

                val totalInternal = internalStats.totalBytes
                val availableInternal = internalStats.availableBytes
                val totalExternal = externalStats.totalBytes
                val availableExternal = externalStats.availableBytes

                return StorageInfo(
                    totalInternalStorage = totalInternal,
                    availableInternalStorage = availableInternal,
                    totalExternalStorage = totalExternal,
                    availableExternalStorage = availableExternal,
                    isExternalStorageAvailable = totalExternal > 0,
                    storageUsagePercentage =
                        calculateStorageUsagePercentage(
                            totalInternal,
                            availableInternal,
                        ),
                )
            }

            override fun getBatteryLevel(): Int {
                val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                return batteryStatus?.let { intent ->
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    (level * 100 / scale.toFloat()).roundToInt()
                } ?: -1
            }

            override fun getAvailableMemory(): Long {
                val runtime = Runtime.getRuntime()
                return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())
            }

            override fun registerCallback(callback: ISystemCallback) {
                callbacks.add(callback)
            }

            override fun unregisterCallback(callback: ISystemCallback) {
                callbacks.remove(callback)
            }

            override fun startMonitoring() {
                if (!isMonitoring) {
                    registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    registerReceiver(storageReceiver, IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW))
                    isMonitoring = true
                }
            }

            override fun stopMonitoring() {
                if (isMonitoring) {
                    try {
                        unregisterReceiver(batteryReceiver)
                        unregisterReceiver(storageReceiver)
                    } catch (e: Exception) {
                        Log.e("SystemInfoService", "Error unregistering receivers", e)
                    }
                    isMonitoring = false
                }
            }
        }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        binder.stopMonitoring()
        callbacks.clear()
    }

    private fun getTotalMemory(): Long =
        try {
            val reader = File("/proc/meminfo").bufferedReader()
            val line = reader.readLine()
            reader.close()
            line.split("\\s+".toRegex())[1].toLong() * 1024
        } catch (e: Exception) {
            Runtime.getRuntime().maxMemory()
        }

    private fun isCharging(): Boolean {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryStatus?.let { intent ->
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        } ?: false
    }

    private fun calculateStorageUsagePercentage(
        total: Long,
        available: Long,
    ): Int =
        if (total > 0) {
            ((total - available) * 100 / total).toInt()
        } else {
            0
        }
}
