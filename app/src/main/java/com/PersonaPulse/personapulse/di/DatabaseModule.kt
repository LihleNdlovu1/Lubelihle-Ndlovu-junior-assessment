package com.PersonaPulse.personapulse.di

import android.content.Context
import androidx.room.Room
import com.PersonaPulse.personapulse.database.PersonaPulseDatabase
import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.repository.ITodoRepository
import com.PersonaPulse.personapulse.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePersonaPulseDatabase(@ApplicationContext context: Context): PersonaPulseDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PersonaPulseDatabase::class.java,
            "personapulse_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: PersonaPulseDatabase): TodoDao {
        return database.todoDao()
    }

}
