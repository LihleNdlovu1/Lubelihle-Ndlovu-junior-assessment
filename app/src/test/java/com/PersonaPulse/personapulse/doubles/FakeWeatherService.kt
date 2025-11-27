package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.model.WeatherResponse
import com.PersonaPulse.personapulse.network.WeatherService

class FakeWeatherService: WeatherService {
    var shouldThrow = false
    var fakeResponse: WeatherResponse? = null
    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        current: Boolean,
        hourly: String?,
        forecastDays: Int
    ): WeatherResponse {
        if (shouldThrow) {
            throw Exception("Fake network error")
        }
        return fakeResponse ?: throw Exception("FakeWeatherService: No mock data set")
    }
}