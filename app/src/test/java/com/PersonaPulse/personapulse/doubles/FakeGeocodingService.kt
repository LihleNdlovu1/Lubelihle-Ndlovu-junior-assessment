package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.network.GeocodeResult
import com.PersonaPulse.personapulse.network.GeocodingService

class FakeGeocodingService: GeocodingService {
    var shouldThrow = false
    var results: List<GeocodeResult> = emptyList()
    override suspend fun searchCity(
        cityName: String,
        format: String,
        limit: Int
    ): List<GeocodeResult> {
        if(shouldThrow){
            throw Exception("Fake geocoding error")
        }else{
            return results
        }
    }
}