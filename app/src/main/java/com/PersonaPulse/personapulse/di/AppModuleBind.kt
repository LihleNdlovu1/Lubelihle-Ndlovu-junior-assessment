package com.PersonaPulse.personapulse.di

import com.PersonaPulse.personapulse.repository.ITodoRepository
import com.PersonaPulse.personapulse.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBind {
    @Binds
    @Singleton
    abstract fun bindTodoRepository(todoRepositoryImpl: TodoRepository): ITodoRepository
}