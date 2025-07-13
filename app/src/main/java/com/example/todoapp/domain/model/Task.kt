package com.example.todoapp.domain.model

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val isCompleted: Boolean? = false
)