package com.PersonaPulse.personapulse.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.PersonaPulse.personapulse.database.converter.PriorityConverter
import com.PersonaPulse.personapulse.database.converter.RecurrenceConverter
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.Recurrence

@Entity(tableName = "todos")
@TypeConverters(PriorityConverter::class, RecurrenceConverter::class)
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val category: String? = null,
    val priority: Priority = Priority.LOW,
    val recurrence: Recurrence = Recurrence.NONE
)



