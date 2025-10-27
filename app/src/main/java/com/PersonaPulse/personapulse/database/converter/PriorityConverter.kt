package com.PersonaPulse.personapulse.database.converter

import androidx.room.TypeConverter
import com.PersonaPulse.personapulse.model.Priority

class PriorityConverter {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}



