package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.Task

interface TaskRepository {
    suspend fun addTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun getAllTasks(): List<Task>
}