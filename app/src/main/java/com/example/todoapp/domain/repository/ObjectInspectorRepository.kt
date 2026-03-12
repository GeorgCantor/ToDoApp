package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.InspectionNode

interface ObjectInspectorRepository {
    fun inspect(
        obj: Any?,
        name: String = "Root",
    ): InspectionNode

    fun getNodeById(id: String): InspectionNode?

    fun clearCache()
}
