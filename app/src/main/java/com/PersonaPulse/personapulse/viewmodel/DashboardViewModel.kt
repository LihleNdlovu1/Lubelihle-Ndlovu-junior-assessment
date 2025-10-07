package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.model.WeatherResponse
import com.PersonaPulse.personapulse.network.WeatherService
import kotlin.random.Random

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    // Todo-related state
    private val _todos = MutableStateFlow<List<TodoData>>(emptyList())
    val todos: StateFlow<List<TodoData>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Weather-related state
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()
    
    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()
    
    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()
    
    private val _selectedCity = MutableStateFlow<String?>(null)
    val selectedCity: StateFlow<String?> = _selectedCity.asStateFlow()
    
    init {
        loadMockData()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Mock todo data
            val mockTodos = listOf(
                TodoData(
                    title = "Complete project proposal",
                    description = "Draft and review the Q4 project proposal",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work"
                ),
                TodoData(
                    title = "Grocery shopping",
                    description = "Buy ingredients for weekend cooking",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Personal"
                ),
                TodoData(
                    title = "Call dentist",
                    description = "Schedule annual checkup",
                    priority = com.PersonaPulse.personapulse.model.Priority.LOW,
                    category = "Health"
                )
            )
            
            _todos.value = mockTodos
            _isLoading.value = false
        }
    }
    
    fun addTodo(todo: TodoData) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            currentTodos.add(todo)
            _todos.value = currentTodos
        }
    }
    
    fun updateTodo(todo: TodoData) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            val index = currentTodos.indexOfFirst { it.id == todo.id }
            if (index != -1) {
                currentTodos[index] = todo
                _todos.value = currentTodos
            }
        }
    }
    
    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            currentTodos.removeAll { it.id == todoId }
            _todos.value = currentTodos
        }
    }
    
    fun toggleTodoCompletion(todoId: String) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            val index = currentTodos.indexOfFirst { it.id == todoId }
            if (index != -1) {
                val todo = currentTodos[index]
                val updatedTodo = todo.copy(
                    isCompleted = !todo.isCompleted,
                    completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
                )
                currentTodos[index] = updatedTodo
                _todos.value = currentTodos
            }
        }
    }
    
    // Additional methods for index-based operations (for compatibility with UI)
    fun toggleTodoCompleted(index: Int) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            if (index in 0 until currentTodos.size) {
                val todo = currentTodos[index]
                val updatedTodo = todo.copy(
                    isCompleted = !todo.isCompleted,
                    completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
                )
                currentTodos[index] = updatedTodo
                _todos.value = currentTodos
            }
        }
    }
    
    fun deleteTodo(index: Int) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            if (index in 0 until currentTodos.size) {
                currentTodos.removeAt(index)
                _todos.value = currentTodos
            }
        }
    }
    
    fun addTodo(title: String, description: String?, priority: com.PersonaPulse.personapulse.model.Priority, dueDate: Long?) {
        viewModelScope.launch {
            val newTodo = TodoData(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                category = "General"
            )
            val currentTodos = _todos.value.toMutableList()
            currentTodos.add(newTodo)
            _todos.value = currentTodos
        }
    }
    
    fun updateTodo(index: Int, title: String, description: String?, priority: com.PersonaPulse.personapulse.model.Priority, dueDate: Long?) {
        viewModelScope.launch {
            val currentTodos = _todos.value.toMutableList()
            if (index in 0 until currentTodos.size) {
                val existingTodo = currentTodos[index]
                val updatedTodo = existingTodo.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate
                )
                currentTodos[index] = updatedTodo
                _todos.value = currentTodos
            }
        }
    }
    
    fun fetchWeather() {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            _weatherError.value = null
            
            try {
                // Mock weather data for now
                val mockWeather = WeatherResponse(
                    latitude = -26.2041,
                    longitude = 28.0473,
                    generationtime_ms = 1234567890.0,
                    utc_offset_seconds = 7200,
                    timezone = "Africa/Johannesburg",
                    timezone_abbreviation = "SAST",
                    elevation = 1753.0,
                    current_weather = com.PersonaPulse.personapulse.model.CurrentWeather(
                        temperature = 24.0 + Random.nextDouble(-5.0, 10.0),
                        windspeed = 5.0 + Random.nextDouble(0.0, 15.0),
                        winddirection = Random.nextDouble(0.0, 360.0),
                        weathercode = Random.nextInt(0, 100),
                        is_day = 1,
                        time = "2024-01-01T12:00"
                    )
                )
                
                _weather.value = mockWeather
            } catch (e: Exception) {
                _weatherError.value = e.message ?: "Unknown error occurred"
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }
    
    fun setSelectedCity(city: String) {
        _selectedCity.value = city
    }
    
    fun getCompletedTodosCount(): Int {
        return _todos.value.count { it.isCompleted }
    }
    
    fun getTotalTodosCount(): Int {
        return _todos.value.size
    }
    
    fun getHighPriorityTodosCount(): Int {
        return _todos.value.count { it.priority == com.PersonaPulse.personapulse.model.Priority.HIGH }
    }
}
