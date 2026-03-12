package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.ObjectInspectorRepository

class GetNodeByIdUseCase(
    private val repository: ObjectInspectorRepository,
) {
    operator fun invoke(id: String) = repository.getNodeById(id)
}
