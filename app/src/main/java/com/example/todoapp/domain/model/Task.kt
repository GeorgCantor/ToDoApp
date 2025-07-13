package com.example.todoapp.domain.model

import com.example.todoapp.data.local.TaskEntity

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
) {
    fun toEntity() = TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted
    )
}