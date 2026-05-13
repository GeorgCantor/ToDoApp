package com.example.todoapp.presentation.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.Stroke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DrawingViewModel : ViewModel() {
    private val _strokes = MutableStateFlow<List<Stroke>>(emptyList())
    val strokes = _strokes.asStateFlow()

    private val _currentColor = MutableStateFlow(Color.Black)
    val currentColor = _currentColor.asStateFlow()

    private val _currentStrokeWidth = MutableStateFlow(10F)
    val currentStrokeWidth = _currentStrokeWidth.asStateFlow()

    private val _isEraserMode = MutableStateFlow(false)
    val isEraserMode = _isEraserMode.asStateFlow()

    private val backgroundColor = Color.White

    fun setColor(color: Color) {
        _currentColor.update { color }
        if (_isEraserMode.value) toggleEraserMode()
    }

    fun setStrokeWidth(width: Float) {
        _currentStrokeWidth.update { width }
    }

    fun toggleEraserMode() {
        _isEraserMode.update { !it }
    }

    fun addStroke(points: List<Offset>) {
        if (points.size < 2) return
        val stroke =
            Stroke(
                points = points,
                color = if (_isEraserMode.value) backgroundColor else _currentColor.value,
                strokeWidth = _currentStrokeWidth.value,
            )
        viewModelScope.launch {
            _strokes.update { it + stroke }
        }
    }

    fun clearCanvas() {
        viewModelScope.launch {
            _strokes.update { emptyList() }
        }
    }
}
