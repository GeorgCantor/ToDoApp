package com.example.todoapp.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.R
import com.example.todoapp.domain.model.MapStyleType
import com.example.todoapp.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var fusedLocationClient: FusedLocationProviderClient? by remember { mutableStateOf(null) }
    var selectedMapType by remember { mutableStateOf(MapStyleType.NORMAL) }
    var showMapTypeSelector by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val hasPermission = locationPermissionState.status.isGranted

    LaunchedEffect(Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                context.showToast(e.message.orEmpty())
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && hasPermission) {
                    try {
                        fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                            location?.let {
                                userLocation = LatLng(it.latitude, it.longitude)
                            }
                        }
                    } catch (e: SecurityException) {
                        context.showToast(e.message.orEmpty())
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val defaultLocation = LatLng(55.751244, 37.618423)
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLocation ?: defaultLocation, 12f)
        }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 12f)
        }
    }

    if (!hasPermission) {
        LaunchedEffect(Unit) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val mapProperties =
        remember(selectedMapType) {
            MapProperties(mapType = selectedMapType.mapType, isMyLocationEnabled = hasPermission)
        }

    val mapUiSettings = remember { MapUiSettings(compassEnabled = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMapTypeSelector = !showMapTypeSelector },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = androidx.compose.foundation.shape.CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Изменить тип карты",
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
            ) {
                userLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = context.getString(R.string.your_location),
                        snippet = context.getString(R.string.current_position),
                    )
                }

                if (userLocation == null) {
                    Marker(
                        state = MarkerState(position = defaultLocation),
                        title = context.getString(R.string.moscow),
                        snippet = context.getString(R.string.default_location),
                    )
                }
            }

            if (showMapTypeSelector) {
                MapTypeSelector(
                    selectedType = selectedMapType,
                    onTypeSelected = {
                        selectedMapType = it
                        showMapTypeSelector = false
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                )
            }
        }
    }
}

@Composable
fun MapTypeSelector(
    selectedType: MapStyleType,
    onTypeSelected: (MapStyleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Тип карты",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            MapStyleType.entries.forEach { mapType ->
                MapTypeItem(
                    mapType = mapType,
                    isSelected = selectedType == mapType,
                    onClick = { onTypeSelected(mapType) },
                )
            }
        }
    }
}

@Composable
fun MapTypeItem(
    mapType: MapStyleType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = mapType.icon,
                contentDescription = mapType.title,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = mapType.title,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
            )

            if (isSelected) {
                Spacer(modifier = Modifier.weight(1F))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выбрано",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
