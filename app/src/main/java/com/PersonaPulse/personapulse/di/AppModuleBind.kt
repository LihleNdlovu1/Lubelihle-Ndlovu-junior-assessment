package com.PersonaPulse.personapulse.di

import com.PersonaPulse.personapulse.notification.INotificationManager
import com.PersonaPulse.personapulse.notification.INotificationService
import com.PersonaPulse.personapulse.notification.NotificationManager
import com.PersonaPulse.personapulse.notification.NotificationService
import com.PersonaPulse.personapulse.repository.ITodoRepository
import com.PersonaPulse.personapulse.repository.TodoRepository
import com.PersonaPulse.personapulse.utils.ILocationManager
import com.PersonaPulse.personapulse.utils.LocationManager
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
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepository
    ): ITodoRepository

    @Binds
    @Singleton
    abstract fun bindLocationManager(
        locationManager: LocationManager
    ): ILocationManager

    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        notificationManager: NotificationManager
    ): INotificationManager

    @Binds
    @Singleton
    abstract fun bindNotificationService(
        notificationService: NotificationService
    ): INotificationService
}

