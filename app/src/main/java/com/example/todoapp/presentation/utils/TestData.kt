package com.example.todoapp.presentation.utils

import java.util.Date

object TestData {

    val simpleObjects = listOf(
        "Simple Object" to "Hello World",
        "Number" to 42,
        "Boolean" to true,
        "List" to listOf(1, 2, 3, 4, 5),
        "Map" to mapOf(
            "name" to "John",
            "age" to 30,
            "active" to true
        )
    )

    val complexObject = createComplexObject()

    private fun createComplexObject(): Any {
        data class Address(
            val street: String,
            val city: String,
            val zipCode: Int,
            val coordinates: Pair<Double, Double>
        )

        data class Person(
            val name: String,
            val age: Int,
            private val secretKey: String,
            val address: Address,
            val hobbies: List<String>,
            val metadata: Map<String, Any>,
            val createdAt: Date,
            val tags: Array<String>
        )

        return Person(
            name = "John Doe",
            age = 30,
            secretKey = "supersecret123",
            address = Address(
                street = "Main Street 123",
                city = "New York",
                zipCode = 10001,
                coordinates = 40.7128 to -74.0060
            ),
            hobbies = listOf("Programming", "Chess", "Hiking", "Photography"),
            metadata = mapOf(
                "membership" to "premium",
                "points" to 15420,
                "verified" to true,
                "lastLogin" to Date()
            ),
            createdAt = Date(2020, 1, 1),
            tags = arrayOf("developer", "mentor", "kotlin", "android")
        )
    }
}