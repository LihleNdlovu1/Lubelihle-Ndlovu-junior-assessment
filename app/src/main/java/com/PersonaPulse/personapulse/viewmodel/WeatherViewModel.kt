package com.PersonaPulse.personapulse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.PersonaPulse.personapulse.model.WeatherResponse
import com.PersonaPulse.personapulse.network.GeocodingService
import com.PersonaPulse.personapulse.network.WeatherService
import com.PersonaPulse.personapulse.utils.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherService: WeatherService,
    private val geocodingService: GeocodingService,
    private val locationManager: LocationManager
) : ViewModel() {
    
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
    
    private val _isUsingCurrentLocation = MutableStateFlow(false)
    val isUsingCurrentLocation: StateFlow<Boolean> = _isUsingCurrentLocation.asStateFlow()
    
    init {
        fetchWeather()
    }
    
    fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            _weatherError.value = null
            
            try {
                val locationResult = locationManager.getCurrentLocation()
                locationResult.onSuccess { location ->
                    _isUsingCurrentLocation.value = true
                    _selectedCity.value = "Current Location"
                    fetchWeather(location.latitude, location.longitude)
                }.onFailure { error ->
                    _weatherError.value = when (error) {
                        is SecurityException -> "Location permission not granted"
                        else -> "Failed to get location: ${error.message}"
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _weatherError.value = "Failed to get location: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    fun fetchWeather(latitude: Double = -26.2041, longitude: Double = 28.0473) {
        viewModelScope.launch {
            _isLoading.value = true
            _weatherError.value = null
            
            try {
                Log.d("WeatherViewModel", "Fetching weather for lat=$latitude, lon=$longitude")
                // Fetch real weather data from API with hourly forecast
                val weatherData = weatherService.getCurrentWeather(
                    latitude = latitude,
                    longitude = longitude,
                    current = true,
                    hourly = "temperature_2m,weathercode,windspeed_10m",
                    forecastDays = 1
                )
                Log.d("WeatherViewModel", "Weather data received: $weatherData")
                _weather.value = weatherData
                weatherData.current_weather?.let { generateOutfitSuggestion(weatherData) }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Failed to fetch weather", e)
                _weatherError.value = "Failed to fetch weather: ${e.message}"
                // Fallback to mock data on error
                val mockWeather = createMockWeatherData()
                _weather.value = mockWeather
                generateOutfitSuggestion(mockWeather)
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
        val temperature = weather.current_weather?.temperature ?: return
        val weatherCode = weather.current_weather?.weathercode ?: return
        
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
        _isUsingCurrentLocation.value = false
        viewModelScope.launch {
            _isLoading.value = true
            _weatherError.value = null
            
            try {
                // Get coordinates for the city
                val geocodeResults = geocodingService.searchCity(city)
                if (geocodeResults.isNotEmpty()) {
                    val result = geocodeResults[0]
                    val latitude = result.lat.toDouble()
                    val longitude = result.lon.toDouble()
                    fetchWeather(latitude, longitude)
                } else {
                    _weatherError.value = "City not found"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _weatherError.value = "Failed to find city: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun hasLocationPermission(): Boolean {
        return locationManager.hasLocationPermission()
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
