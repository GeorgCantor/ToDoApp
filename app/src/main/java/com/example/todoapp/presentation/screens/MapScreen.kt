package com.example.todoapp.presentation.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.R
import com.example.todoapp.domain.model.LocationPoint
import com.example.todoapp.domain.model.MapStyleType
import com.example.todoapp.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = koinViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedMapType by remember { mutableStateOf(MapStyleType.NORMAL) }
    var showMapTypeSelector by remember { mutableStateOf(false) }
    var showHistoryPanel by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val hasPermission = locationPermissionState.status.isGranted

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.getCurrentLocation(forceExact = false)
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(state.currentLocation) {
        state.currentLocation?.let { location ->
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
        }
    }

    val mapProperties =
        remember(selectedMapType, hasPermission) {
            MapProperties(
                mapType = selectedMapType.mapType,
                isMyLocationEnabled = hasPermission,
            )
        }

    val mapUiSettings =
        remember {
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
            )
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map)) },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                actions = {
                    IconButton(onClick = { showHistoryPanel = !showHistoryPanel }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = stringResource(R.string.history),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                FloatingActionButton(
                    onClick = { viewModel.requestExactLocation() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    if (state.isLoadingLocation) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.get_exact_location),
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showMapTypeSelector = !showMapTypeSelector },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    shape = androidx.compose.foundation.shape.CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.change_map_type),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
            ) {
                state.currentLocation?.let { location ->
                    Marker(
                        state =
                            MarkerState(
                                position = LatLng(location.latitude, location.longitude),
                            ),
                        title = stringResource(R.string.your_location),
                        snippet = stringResource(R.string.current_position),
                        alpha = 1f,
                    )
                }

                state.historyPoints.forEach { point ->
                    Marker(
                        state =
                            MarkerState(
                                position = LatLng(point.latitude, point.longitude),
                            ),
                        title = formatMarkerTitle(point.source.name),
                        snippet = formatMarkerSnippet(point.timestamp, point.accuracy),
                        alpha = 0.7f,
                    )
                }
            }

            if (showHistoryPanel) {
                HistoryPanel(
                    points = state.historyPoints,
                    isLoading = state.isLoadingHistory,
                    onDismiss = { showHistoryPanel = false },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                )
            }

            if (showMapTypeSelector) {
                MapTypeSelector(
                    selectedType = selectedMapType,
                    onTypeSelected = { type ->
                        selectedMapType = type
                        showMapTypeSelector = false
                    },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                )
            }

            if (state.showLocationSettingsDialog) {
                LocationSettingsDialog(
                    onConfirm = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        viewModel.dismissLocationSettingsDialog()
                    },
                    onDismiss = { viewModel.dismissLocationSettingsDialog() },
                )
            }
        }
    }
}

@Composable
fun HistoryPanel(
    points: List<LocationPoint>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(400.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.location_history),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.close),
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (points.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.no_history_points),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(1f),
                ) {
                    items(points.size) { index ->
                        HistoryItem(point = points[index])
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(point: LocationPoint) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector =
                        when (point.source.name) {
                            "GPS" -> Icons.Default.LocationOn
                            else -> Icons.Default.List
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDateTime(point.timestamp),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text =
                    String.format(
                        Locale.getDefault(),
                        "%.6f, %.6f",
                        point.latitude,
                        point.longitude,
                    ),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Source: ${point.source.name}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                point.accuracy?.let {
                    Text(
                        text = "±${it.toInt()}m",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun LocationSettingsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.location_disabled)) },
        text = { Text(stringResource(R.string.location_disabled_message)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.open_settings))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
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
                text = stringResource(R.string.map_type),
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
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
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
                    contentDescription = stringResource(R.string.selected),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

private fun formatMarkerTitle(source: String): String =
    when (source) {
        "GPS" -> "GPS Location"
        "IP_API" -> "IP Location"
        else -> "Historical Point"
    }

private fun formatMarkerSnippet(
    timestamp: Long,
    accuracy: Float?,
): String {
    val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    val date = dateFormat.format(Date(timestamp))
    return if (accuracy != null) {
        "$date (±${accuracy.toInt()}m)"
    } else {
        date
    }
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
