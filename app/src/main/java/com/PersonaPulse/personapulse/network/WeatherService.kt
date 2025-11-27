package com.PersonaPulse.personapulse.network

import com.PersonaPulse.personapulse.model.WeatherResponse
interface WeatherService {
    @retrofit2.http.GET("v1/forecast")
    suspend fun getCurrentWeather(
        @retrofit2.http.Query("latitude") latitude: Double,
        @retrofit2.http.Query("longitude") longitude: Double,
        @retrofit2.http.Query("current_weather") current: Boolean = true,
        @retrofit2.http.Query("hourly") hourly: String? = null,
        @retrofit2.http.Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}
