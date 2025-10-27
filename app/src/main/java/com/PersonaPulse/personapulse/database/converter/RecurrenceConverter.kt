package com.PersonaPulse.personapulse.database.converter

import androidx.room.TypeConverter
import com.PersonaPulse.personapulse.model.Recurrence

class RecurrenceConverter {
    @TypeConverter
    fun fromRecurrence(recurrence: Recurrence): String {
        return recurrence.name
    }

    @TypeConverter
    fun toRecurrence(recurrence: String): Recurrence {
        return Recurrence.valueOf(recurrence)
    }
}



