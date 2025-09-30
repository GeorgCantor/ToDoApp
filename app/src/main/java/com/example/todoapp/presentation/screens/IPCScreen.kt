package com.example.todoapp.presentation.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.ISystemInfoService
import com.example.todoapp.StorageInfo
import com.example.todoapp.SystemInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPCScreen(navController: NavController) {
    val context = LocalContext.current
    var systemInfo by remember { mutableStateOf<SystemInfo?>(null) }
    var storageInfo by remember { mutableStateOf<StorageInfo?>(null) }
    var isConnected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var systemInfoService: ISystemInfoService? by remember { mutableStateOf(null) }

    fun loadSystemInfo() {
        coroutineScope.launch {
            try {
                systemInfo = systemInfoService?.getSystemInfo()
                storageInfo = systemInfoService?.getStorageInfo()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error loading system info: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    val serviceConnection =
        remember {
            object : ServiceConnection {
                override fun onServiceConnected(
                    name: ComponentName?,
                    service: IBinder?,
                ) {
                    systemInfoService = ISystemInfoService.Stub.asInterface(service)
                    isConnected = true
                    errorMessage = null

                    coroutineScope.launch {
                        systemInfoService?.startMonitoring()
                        loadSystemInfo()
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    systemInfoService = null
                    isConnected = false
                }
            }
        }

    fun connectToService() {
        try {
            isLoading = true
            val intent =
                Intent().apply {
                    component =
                        ComponentName(
                            "com.example.todoapp",
                            "com.example.todoapp.service.SystemInfoService",
                        )
                }
            val bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            if (!bound) {
                errorMessage = "Failed to bind to service"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            isLoading = false
        }
    }

    fun disconnectFromService() {
        try {
            systemInfoService?.stopMonitoring()
            context.unbindService(serviceConnection)
            systemInfoService = null
            isConnected = false
        } catch (e: Exception) {
            errorMessage = "Error disconnecting: ${e.message}"
        }
    }

    LaunchedEffect(isConnected) {
        if (isConnected) {
            while (isConnected) {
                loadSystemInfo()
                delay(5000)
            }
        }
    }

    LaunchedEffect(Unit) {
        connectToService()
    }

    DisposableEffect(Unit) {
        onDispose {
            disconnectFromService()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("System Info IPC") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Service Status",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = if (isConnected) "Connected" else "Disconnected",
                        color =
                            if (isConnected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { connectToService() },
                    enabled = !isConnected && !isLoading,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Connect")
                }
                Button(
                    onClick = { disconnectFromService() },
                    enabled = isConnected,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Disconnect")
                }
                Button(
                    onClick = { loadSystemInfo() },
                    enabled = isConnected,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Refresh")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            systemInfo?.let { info ->
                SystemInfoCard(systemInfo = info)
            }

            storageInfo?.let { info ->
                StorageInfoCard(storageInfo = info)
            }
        }
    }
}

@Composable
fun SystemInfoCard(systemInfo: SystemInfo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Build, contentDescription = "System")
                Text(
                    text = "System Information",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow("Device", systemInfo.deviceName)
            InfoRow("Android", "${systemInfo.androidVersion} (API ${systemInfo.apiLevel})")
            InfoRow("Manufacturer", systemInfo.manufacturer)
            InfoRow("Model", systemInfo.model)
            InfoRow("Battery", "${systemInfo.batteryLevel}%", Icons.Default.Menu)
            InfoRow("Charging", if (systemInfo.isCharging) "Yes" else "No")
            InfoRow("Available Memory", formatBytes(systemInfo.availableMemory))
            InfoRow("Total Memory", formatBytes(systemInfo.totalMemory))
            InfoRow(
                "Memory Usage",
                "${((systemInfo.totalMemory - systemInfo.availableMemory) * 100 / systemInfo.totalMemory).toInt()}%",
            )
            InfoRow("Last Updated", formatTimestamp(systemInfo.timestamp))
        }
    }
}

@Composable
fun StorageInfoCard(storageInfo: StorageInfo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Storage")
                Text(
                    text = "Storage Information",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow("Internal Total", formatBytes(storageInfo.totalInternalStorage))
            InfoRow("Internal Available", formatBytes(storageInfo.availableInternalStorage))
            InfoRow("Internal Usage", "${storageInfo.storageUsagePercentage}%")

            LinearProgressIndicator(
                progress = { storageInfo.storageUsagePercentage / 100f },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                color =
                    when {
                        storageInfo.storageUsagePercentage > 90 -> MaterialTheme.colorScheme.error
                        storageInfo.storageUsagePercentage > 75 -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primary
                    },
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (storageInfo.isExternalStorageAvailable) {
                InfoRow(
                    "External Total",
                    formatBytes(storageInfo.totalExternalStorage),
                    Icons.Default.Info,
                )
                InfoRow("External Available", formatBytes(storageInfo.availableExternalStorage))
                val externalUsage =
                    if (storageInfo.totalExternalStorage > 0) {
                        ((storageInfo.totalExternalStorage - storageInfo.availableExternalStorage) * 100 / storageInfo.totalExternalStorage)
                            .toInt()
                    } else {
                        0
                    }
                InfoRow("External Usage", "$externalUsage%")

                LinearProgressIndicator(
                    progress = { externalUsage / 100f },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    color =
                        when {
                            externalUsage > 90 -> MaterialTheme.colorScheme.error
                            externalUsage > 75 -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.primary
                        },
                )
            } else {
                InfoRow("External Storage", "Not Available", Icons.Default.Warning)
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = label,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        )
    }
}

fun formatBytes(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    return "%.2f %s".format(size, units[unitIndex])
}

fun formatTimestamp(timestamp: Long): String =
    java.text
        .SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        .format(java.util.Date(timestamp))
