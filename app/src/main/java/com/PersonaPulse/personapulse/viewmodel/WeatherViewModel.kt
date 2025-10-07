package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.WeatherResponse
import com.PersonaPulse.personapulse.network.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()
    
    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _selectedCity = MutableStateFlow<String>("Johannesburg")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()
    
    private val _outfitSuggestion = MutableStateFlow<String?>(null)
    val outfitSuggestion: StateFlow<String?> = _outfitSuggestion.asStateFlow()
    
    init {
        fetchWeather()
    }
    
    fun fetchWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _weatherError.value = null
            
            try {
                // For demo purposes, we'll use mock data instead of real API calls
                val mockWeather = createMockWeatherData()
                _weather.value = mockWeather
                generateOutfitSuggestion(mockWeather)
            } catch (e: Exception) {
                _weatherError.value = "Failed to fetch weather: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun createMockWeatherData(): WeatherResponse {
        val temperature = Random.nextInt(-5, 35) // Random temperature between -5°C and 35°C
        val weatherCode = Random.nextInt(0, 3) // 0: clear, 1: cloudy, 2: rainy
        
        return WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = com.PersonaPulse.personapulse.model.CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1, // Assume day time for demo
                time = System.currentTimeMillis().toString()
            )
        )
    }
    
    private fun generateOutfitSuggestion(weather: WeatherResponse) {
        val temperature = weather.current_weather.temperature
        val weatherCode = weather.current_weather.weathercode
        
        val suggestion = when {
            temperature < 0 -> "Bundle up! Wear a heavy coat, gloves, and a warm hat. It's freezing!"
            temperature < 10 -> "Cold weather! Wear a warm jacket, long pants, and closed shoes."
            temperature < 20 -> "Cool weather. A light jacket or sweater would be perfect."
            temperature < 25 -> "Pleasant weather! Light clothing like a t-shirt and jeans would be comfortable."
            temperature < 30 -> "Warm weather! Shorts and a t-shirt would be ideal. Don't forget sunscreen!"
            else -> "Hot weather! Light, breathable clothing and definitely sunscreen!"
        }
        
        val weatherCondition = when (weatherCode) {
            0 -> "Clear skies"
            1 -> "Partly cloudy"
            2 -> "Cloudy"
            else -> "Rainy weather"
        }
        
        _outfitSuggestion.value = "$suggestion Weather: $weatherCondition"
    }
    
    fun updateCity(city: String) {
        _selectedCity.value = city
        fetchWeather()
    }
    
    fun refreshWeather() {
        fetchWeather()
    }
    
    fun getWeatherIcon(): String {
        return when (_weather.value?.current_weather?.weathercode) {
            0 -> "☀️" // Clear
            1 -> "⛅" // Partly cloudy
            2 -> "☁️" // Cloudy
            else -> "🌧️" // Rainy
        }
    }
    
    fun getTemperatureColor(): String {
        val temperature = _weather.value?.current_weather?.temperature ?: 0.0
        return when {
            temperature < 0 -> "#87CEEB" // Light blue for cold
            temperature < 10 -> "#ADD8E6" // Light blue
            temperature < 20 -> "#90EE90" // Light green
            temperature < 25 -> "#FFD700" // Gold
            temperature < 30 -> "#FFA500" // Orange
            else -> "#FF6347" // Tomato red for hot
        }
    }
}
