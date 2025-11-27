package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.utils.ILocationManager
import com.PersonaPulse.personapulse.utils.LocationData
import kotlinx.coroutines.flow.Flow

class FakeLocationManager : ILocationManager{
    var hasPermission = true
    var locationResult: Result<LocationData> =
        Result.success(LocationData(latitude = 0.0, longitude = 0.0))

    override fun hasLocationPermission(): Boolean {
        return hasPermission
    }

    override suspend fun getCurrentLocation(): Result<LocationData> {
        return locationResult
    }



}