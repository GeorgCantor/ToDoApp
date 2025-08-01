package com.example.todoapp.presentation.screens

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.todoapp.utils.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationPermissionState =
        rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var fusedLocationClient: FusedLocationProviderClient? by remember { mutableStateOf(null) }
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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        userLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Ваше местоположение",
                snippet = "Текущая позиция",
            )
        }

        if (userLocation == null) {
            Marker(
                state = MarkerState(position = defaultLocation),
                title = "Москва",
                snippet = "Местоположение по умолчанию",
            )
        }
    }
}
