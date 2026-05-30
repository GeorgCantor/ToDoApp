package com.example.todoapp.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Stroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
)
