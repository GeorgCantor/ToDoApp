package com.example.todoapp.presentation.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

data class BleDevice(
    val name: String?,
    val address: String?,
    val rssi: Int?
)

@SuppressLint("MissingPermission")
@Composable
fun BleScanScreen() {
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    var devices by remember { mutableStateOf<List<BleDevice>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var hasBlePermission by remember { mutableStateOf(false) }
    val isBleSupported by remember {
        mutableStateOf(context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> hasBlePermission = permissions.all { it.value } }

    LaunchedEffect(Unit) {
        if (isBleSupported) {
            val permissions = mutableListOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.addAll(
                    listOf(
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            }
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val device = BleDevice(
                name = result?.device?.name,
                address = result?.device?.address,
                rssi = result?.rssi
            )
            devices = devices.toMutableList().apply {
                val existingIndex = indexOfFirst { it.address == device.address }
                if (existingIndex >= 0) {
                    set(existingIndex, device)
                } else {
                    add(device)
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                bluetoothLeScanner?.stopScan(scanCallback)
                isScanning = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            bluetoothLeScanner?.stopScan(scanCallback)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isBleSupported) {
                Text("BLE не поддерживается на этом устройстве")
            } else if (!hasBlePermission) {
                Text("Необходимы разрешения для сканирования BLE")
            } else {
                Button(
                    onClick = {
                        if (isScanning) {
                            bluetoothLeScanner?.stopScan(scanCallback)
                            isScanning = false
                        } else {
                            devices = emptyList()
                            val settings = ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build()
                            bluetoothLeScanner?.startScan(null, settings, scanCallback)
                            isScanning = true
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(if (isScanning) "Остановить сканирование" else "Начать сканирование")
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(devices) { device ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = device.name ?: "Unknown device",
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(text = "MAC: ${device.address}")
                            Text(text = "RSSI: ${device.rssi} dBm")
                        }
                    }
                }
            }
        }
    }
}