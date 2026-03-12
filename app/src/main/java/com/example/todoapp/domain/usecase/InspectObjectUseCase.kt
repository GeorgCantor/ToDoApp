package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.InspectionNode
import com.example.todoapp.domain.repository.ObjectInspectorRepository

class InspectObjectUseCase(
    private val repository: ObjectInspectorRepository,
) {
    operator fun invoke(
        obj: Any?,
        name: String = "Root",
    ): Result<InspectionNode> =
        try {
            if (obj == null) Result.success(repository.inspect(null))
            val node = repository.inspect(obj, name)
            Result.success(node)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
