package com.PersonaPulse.personapulse.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.model.WeatherResponse
import com.PersonaPulse.personapulse.model.CurrentWeather
import com.PersonaPulse.personapulse.network.GeocodingService
import com.PersonaPulse.personapulse.network.WeatherService
import com.PersonaPulse.personapulse.notification.NotificationManager
import com.PersonaPulse.personapulse.repository.TodoRepository
import com.PersonaPulse.personapulse.utils.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notificationManager: NotificationManager,
    private val weatherService: WeatherService,
    private val geocodingService: GeocodingService,
    private val locationManager: LocationManager
) : ViewModel() {
    
    // Todo-related state - now using Room database
    val todos = todoRepository.getAllTodos()
    val incompleteTodos = todoRepository.getIncompleteTodos()
    val completedTodos = todoRepository.getCompletedTodos()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    // Weather-related state
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()
    
    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()
    
    private val _isLoadingWeather = MutableStateFlow(false)
    val isLoadingWeather: StateFlow<Boolean> = _isLoadingWeather.asStateFlow()
    
    private val _selectedCity = MutableStateFlow("Johannesburg")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()
    
    private val _outfitSuggestion = MutableStateFlow<String?>(null)
    val outfitSuggestion: StateFlow<String?> = _outfitSuggestion.asStateFlow()
    
    init {
        // Initialize other data as needed
        fetchWeather("Johannesburg")
    }
    
    fun toggleTodoCompleted(todo: TodoData) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(
                    isCompleted = !todo.isCompleted,
                    completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
                )
                todoRepository.updateTodo(updatedTodo)
                _successMessage.value = if (updatedTodo.isCompleted) "Task completed!" else "Task marked as incomplete"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
    
    fun deleteTodo(todo: TodoData) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo)
                _successMessage.value = "Task deleted successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun addTodo(title: String, description: String?, priority: com.PersonaPulse.personapulse.model.Priority, dueDate: Long?, category: String? = null, reminderTime: Long? = null) {
        viewModelScope.launch {
            try {
                // Validate input
                if (title.isBlank()) {
                    _errorMessage.value = "Task title cannot be empty"
                    return@launch
                }
                
                val newTodo = TodoData(
                    title = title.trim(),
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    priority = priority,
                    dueDate = dueDate,
                    category = category,
                    reminderTime = reminderTime
                )
                todoRepository.insertTodo(newTodo)
                _successMessage.value = "Task created successfully"
                
                // Schedule notifications if due date is set
                if (dueDate != null) {
                    notificationManager.scheduleTaskReminder(newTodo)
                    notificationManager.scheduleOverdueCheck(newTodo)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun updateTodo(todo: TodoData, title: String, description: String?, priority: com.PersonaPulse.personapulse.model.Priority, dueDate: Long?, category: String? = null, reminderTime: Long? = null) {
        viewModelScope.launch {
            try {
                // Validate input
                if (title.isBlank()) {
                    _errorMessage.value = "Task title cannot be empty"
                    return@launch
                }
                
                val updatedTodo = todo.copy(
                    title = title.trim(),
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    priority = priority,
                    dueDate = dueDate,
                    category = category,
                    reminderTime = reminderTime
                )
                todoRepository.updateTodo(updatedTodo)
                _successMessage.value = "Task updated successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            _weatherError.value = null
            _selectedCity.value = city
            try {
                Log.d("DashboardViewModel", "Fetching weather for city: $city")
                // Get coordinates for the city
                val geocodeResults = geocodingService.searchCity(city)
                if (geocodeResults.isNotEmpty()) {
                    val result = geocodeResults[0]
                    val latitude = result.lat.toDouble()
                    val longitude = result.lon.toDouble()
                    Log.d("DashboardViewModel", "Coordinates: lat=$latitude, lon=$longitude")
                    
                    // Fetch real weather data from API with hourly forecast
                    val weatherData = weatherService.getCurrentWeather(
                        latitude = latitude,
                        longitude = longitude,
                        current = true,
                        hourly = "temperature_2m,weathercode,windspeed_10m",
                        forecastDays = 1
                    )
                    Log.d("DashboardViewModel", "Weather data received: $weatherData")
                    _weather.value = weatherData
                    generateOutfitSuggestion(weatherData)
                } else {
                    Log.w("DashboardViewModel", "City not found: $city")
                    _weatherError.value = "City not found"
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Failed to fetch weather for $city", e)
                _weatherError.value = "Failed to fetch weather: ${e.message}"
                e.printStackTrace()
                // Fallback to mock data on error
                val mockWeather = generateMockWeather(city)
                _weather.value = mockWeather
                generateOutfitSuggestion(mockWeather)
            } finally {
                _isLoadingWeather.value = false
            }
        }
    }
    
    private fun generateMockWeather(city: String): WeatherResponse {
        val temperature = Random.nextInt(10, 30)
        val weatherCode = Random.nextInt(0, 100)
        val isDay = Random.nextInt(0, 2)
        
        return WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = isDay,
                time = System.currentTimeMillis().toString()
            )
        )
    }
    
    private fun generateOutfitSuggestion(weather: WeatherResponse) {
        val temp = weather.current_weather?.temperature ?: return
        val suggestion = when {
            temp < 10 -> "Wear a heavy coat, scarf, and gloves."
            temp < 18 -> "A jacket or sweater would be comfortable."
            temp < 25 -> "Light clothing, maybe a light jacket for the evening."
            else -> "T-shirt and shorts, stay hydrated!"
        }
        _outfitSuggestion.value = suggestion
    }
}