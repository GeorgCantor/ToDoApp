package com.example.todoapp.data.repository

import com.example.todoapp.data.local.TaskDao
import com.example.todoapp.domain.model.Task
import com.example.todoapp.domain.repository.TaskRepository

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {
    override suspend fun addTask(task: Task) {
        dao.insertTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(task.toEntity())
    }

    override suspend fun getAllTasks(): List<Task> {
        return dao.getAllTasks().map { it.toTask() }
    }
}