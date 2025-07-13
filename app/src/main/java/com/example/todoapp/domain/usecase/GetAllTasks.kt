package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.repository.TaskRepository

class GetAllTasks(private val repository: TaskRepository) {
    suspend operator fun invoke() = repository.getAllTasks()
}