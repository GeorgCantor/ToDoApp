package com.example.todoapp.domain.model

data class MapScreenState(
    val currentLocation: LocationResult.Success? = null,
    val historyPoints: List<LocationPoint> = emptyList(),
    val isLoadingLocation: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val error: String? = null,
    val showLocationSettingsDialog: Boolean = false,
)
