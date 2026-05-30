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

    private val _currentStrokeWidth = MutableStateFlow(10f)
    val currentStrokeWidth = _currentStrokeWidth.asStateFlow()

    private val _isEraserMode = MutableStateFlow(false)
    val isEraserMode = _isEraserMode.asStateFlow()

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

    fun addStrokeSegment(points: List<Offset>) {
        if (points.size < 2) return

        if (_isEraserMode.value) {
            eraseStrokesAlongLine(points[0], points[1])
        } else {
            val stroke =
                Stroke(
                    points = points,
                    color = _currentColor.value,
                    strokeWidth = _currentStrokeWidth.value,
                )
            viewModelScope.launch {
                _strokes.update { it + stroke }
            }
        }
    }

    fun addStroke(points: List<Offset>) {
        if (points.size < 2) return

        if (_isEraserMode.value) {
            for (i in 0 until points.lastIndex) {
                eraseStrokesAlongLine(points[i], points[i + 1])
            }
        } else {
            val stroke =
                Stroke(
                    points = points,
                    color = _currentColor.value,
                    strokeWidth = _currentStrokeWidth.value,
                )
            viewModelScope.launch {
                _strokes.update { it + stroke }
            }
        }
    }

    private fun eraseStrokesAlongLine(
        start: Offset,
        end: Offset,
    ) {
        viewModelScope.launch {
            val currentStrokes = _strokes.value
            val eraserRadius = _currentStrokeWidth.value / 2

            val remainingStrokes =
                currentStrokes.filter { stroke ->
                    !isStrokeIntersectingLine(stroke, start, end, eraserRadius)
                }

            _strokes.update { remainingStrokes }
        }
    }

    private fun isStrokeIntersectingLine(
        stroke: Stroke,
        lineStart: Offset,
        lineEnd: Offset,
        eraserRadius: Float,
    ): Boolean {
        for (i in 0 until stroke.points.lastIndex) {
            val strokeStart = stroke.points[i]
            val strokeEnd = stroke.points[i + 1]

            if (distanceBetweenSegments(strokeStart, strokeEnd, lineStart, lineEnd) < eraserRadius) {
                return true
            }
        }
        return false
    }

    private fun distanceBetweenSegments(
        p1: Offset,
        p2: Offset,
        p3: Offset,
        p4: Offset,
    ): Float {
        val dist1 = distanceFromPointToSegment(p1, p3, p4)
        val dist2 = distanceFromPointToSegment(p2, p3, p4)
        val dist3 = distanceFromPointToSegment(p3, p1, p2)
        val dist4 = distanceFromPointToSegment(p4, p1, p2)

        return minOf(dist1, dist2, dist3, dist4)
    }

    private fun distanceFromPointToSegment(
        point: Offset,
        segmentStart: Offset,
        segmentEnd: Offset,
    ): Float {
        val segmentVector = segmentEnd - segmentStart
        val pointVector = point - segmentStart

        val segmentLengthSquared = segmentVector.getDistanceSquared()
        if (segmentLengthSquared == 0f) {
            return (point - segmentStart).getDistance()
        }

        var t = (pointVector.x * segmentVector.x + pointVector.y * segmentVector.y) / segmentLengthSquared
        t = t.coerceIn(0f, 1f)

        val projection =
            Offset(
                segmentStart.x + t * segmentVector.x,
                segmentStart.y + t * segmentVector.y,
            )

        return (point - projection).getDistance()
    }

    fun clearCanvas() {
        viewModelScope.launch {
            _strokes.update { emptyList() }
        }
    }
}
