package com.PersonaPulse.personapulse.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.database.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    com.PersonaPulse.personapulse.database.converter.PriorityConverter::class,
    com.PersonaPulse.personapulse.database.converter.RecurrenceConverter::class
)
abstract class PersonaPulseDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: PersonaPulseDatabase? = null

        fun getDatabase(context: Context): PersonaPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersonaPulseDatabase::class.java,
                    "personapulse_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



