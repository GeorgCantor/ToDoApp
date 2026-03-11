package com.example.todoapp.domain.model

data class ObjectInspectorState(
    val isLoading: Boolean = false,
    val rootNode: InspectionNode? = null,
    val currentNode: InspectionNode? = null,
    val error: String? = null,
)
