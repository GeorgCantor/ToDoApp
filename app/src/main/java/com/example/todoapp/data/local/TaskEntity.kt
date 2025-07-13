package com.example.todoapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false
) {
    fun toTask() = Task(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted
    )
}