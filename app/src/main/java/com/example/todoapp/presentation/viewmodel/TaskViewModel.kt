package com.example.todoapp.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.domain.model.Task
import com.example.todoapp.domain.usecase.AddTask
import com.example.todoapp.domain.usecase.GetAllTasks
import kotlinx.coroutines.launch

class TaskViewModel(
    private val addTask: AddTask,
    private val getAllTasks: GetAllTasks
) : ViewModel() {

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            addTask(Task(title = title, description = description))
            loadTasks()
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            _tasks.clear()
            _tasks.addAll(getAllTasks())
        }
    }
}