package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    private val _todos = MutableStateFlow<List<TodoData>>(emptyList())
    val todos: StateFlow<List<TodoData>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()
    
    init {
        loadMockData()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Mock user profile
            val mockProfile = UserProfile(
                id = "user_123",
                name = "John Doe",
                email = "john.doe@example.com",
                avatarUrl = null,
                joinDate = System.currentTimeMillis() - 86400000 * 30, // 30 days ago
                preferences = UserPreferences(
                    theme = "Dark",
                    notifications = true,
                    language = "English"
                )
            )
            
            // Mock todo data for stats calculation
            val mockTodos = listOf(
                TodoData(
                    title = "Complete project proposal",
                    description = "Draft and review the Q4 project proposal",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 * 2
                ),
                TodoData(
                    title = "Grocery shopping",
                    description = "Buy ingredients for weekend cooking",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Personal",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000
                ),
                TodoData(
                    title = "Call dentist",
                    description = "Schedule annual checkup",
                    priority = com.PersonaPulse.personapulse.model.Priority.LOW,
                    category = "Health",
                    isCompleted = false
                ),
                TodoData(
                    title = "Read book chapter",
                    description = "Finish chapter 5 of 'Clean Code'",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Learning",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 * 5
                )
            )
            
            _userProfile.value = mockProfile
            _todos.value = mockTodos
            calculateUserStats()
            _isLoading.value = false
        }
    }
    
    private fun calculateUserStats() {
        val todos = _todos.value
        val completedTodos = todos.filter { it.isCompleted }
        val totalTodos = todos.size
        val completionRate = if (totalTodos > 0) completedTodos.size.toFloat() / totalTodos else 0f
        
        val highPriorityCompleted = completedTodos.count { it.priority == com.PersonaPulse.personapulse.model.Priority.HIGH }
        val mediumPriorityCompleted = completedTodos.count { it.priority == com.PersonaPulse.personapulse.model.Priority.MEDIUM }
        val lowPriorityCompleted = completedTodos.count { it.priority == com.PersonaPulse.personapulse.model.Priority.LOW }
        
        val stats = UserStats(
            totalTasks = totalTodos,
            completedTasks = completedTodos.size,
            pendingTasks = totalTodos - completedTodos.size,
            completionRate = completionRate,
            highPriorityCompleted = highPriorityCompleted,
            mediumPriorityCompleted = mediumPriorityCompleted,
            lowPriorityCompleted = lowPriorityCompleted,
            streakDays = 7, // Mock streak
            averageTasksPerDay = 2.5f
        )
        
        _userStats.value = stats
    }
    
    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value?.copy(
                name = name,
                email = email
            )
        }
    }
    
    fun updatePreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value?.copy(
                preferences = preferences
            )
        }
    }
    
    fun refreshStats() {
        calculateUserStats()
    }
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val joinDate: Long,
    val preferences: UserPreferences
)

data class UserPreferences(
    val theme: String,
    val notifications: Boolean,
    val language: String
)

data class UserStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val completionRate: Float,
    val highPriorityCompleted: Int,
    val mediumPriorityCompleted: Int,
    val lowPriorityCompleted: Int,
    val streakDays: Int,
    val averageTasksPerDay: Float
)
