package com.example.todoapp.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.maps.android.compose.MapType

enum class MapStyleType(
    val title: String,
    val icon: ImageVector,
    val mapType: MapType,
) {
    NORMAL("Стандартная", Icons.Default.LocationOn, MapType.NORMAL),
    SATELLITE("Спутник", Icons.Default.Home, MapType.SATELLITE),
    HYBRID("Гибрид", Icons.Default.Menu, MapType.HYBRID),
    TERRAIN("Рельеф", Icons.Default.List, MapType.TERRAIN),
}
