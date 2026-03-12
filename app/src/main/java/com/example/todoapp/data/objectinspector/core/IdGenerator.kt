package com.example.todoapp.data.objectinspector.core

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
    private val counter = AtomicLong(0)

    fun generateId() = "node_${counter.incrementAndGet()}"

    fun generateUuid() = "node_${UUID.randomUUID()}"
}
