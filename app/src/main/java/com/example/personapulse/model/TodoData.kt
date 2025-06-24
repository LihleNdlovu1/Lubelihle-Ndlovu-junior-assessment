package com.example.personapulse.model

import kotlinx.serialization.Serializable

@Serializable
data class TodoData(
    val title: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
)
