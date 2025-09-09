package com.PersonaPulse.personapulse.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TodoData(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val category: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val recurrence: Recurrence = Recurrence.NONE
)

@Serializable
enum class Priority { LOW, MEDIUM, HIGH, OVERDUE }

@Serializable
enum class Recurrence { NONE, DAILY, WEEKLY, MONTHLY }
