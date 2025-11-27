package com.PersonaPulse.personapulse.utils

import kotlinx.coroutines.flow.Flow

interface ILocationManager {
    fun hasLocationPermission(): Boolean
    suspend fun getCurrentLocation(): Result<LocationData>
}