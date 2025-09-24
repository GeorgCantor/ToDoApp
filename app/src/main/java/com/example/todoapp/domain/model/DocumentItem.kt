package com.example.todoapp.domain.model

data class DocumentItem(
    val id: String,
    val name: String,
    val type: String,
    val size: String,
    val description: String,
    val downloadUrl: String,
)
