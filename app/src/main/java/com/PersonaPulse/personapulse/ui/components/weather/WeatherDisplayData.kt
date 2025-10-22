package com.PersonaPulse.personapulse.ui.components.weather

/**
 * Single Responsibility: Hold weather display data
 * Only changes when weather data structure changes
 */
data class WeatherDisplayData(
    val location: String,
    val temperature: String,
    val condition: String,
    val feelsLike: String,
    val humidity: String,
    val sunrise: String,
    val sunset: String
)







