package com.PersonaPulse.personapulse.model

import com.google.gson.annotations.SerializedName

//Data Model for the weather API responses
//separated general data from currentWeather
data class WeatherResponse(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationtime_ms: Double,
    @SerializedName("utc_offset_seconds")
    val utc_offset_seconds: Int,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezone_abbreviation: String,
    @SerializedName("elevation")
    val elevation: Double,
    @SerializedName("current_weather")
    val current_weather: CurrentWeather? = null,
    @SerializedName("hourly")
    val hourly: HourlyWeather? = null
)

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("windspeed")
    val windspeed: Double,
    @SerializedName("winddirection")
    val winddirection: Double,
    @SerializedName("weathercode")
    val weathercode: Int,
    @SerializedName("is_day")
    val is_day: Int,
    @SerializedName("time")
    val time: String
)

data class HourlyWeather(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature_2m: List<Double>,
    @SerializedName("weathercode")
    val weathercode: List<Int>,
    @SerializedName("windspeed_10m")
    val windspeed_10m: List<Double>? = null
)