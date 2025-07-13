package com.example.todoapp.domain.usecase

import com.example.todoapp.domain.model.Task
import com.example.todoapp.domain.repository.TaskRepository

class AddTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.addTask(task)
}