package com.PersonaPulse.personapulse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.repository.ITodoRepository
import com.PersonaPulse.personapulse.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val todoRepository: ITodoRepository
) : ViewModel() {
    
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    private val _todos = MutableStateFlow<List<TodoData>>(emptyList())
    val todos: StateFlow<List<TodoData>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()
    
    init {
        viewModelScope.launch {
            _isLoading.value = true
            // In lieu of a real user profile source, keep a minimal default
            _userProfile.value = UserProfile(
                id = "local_user",
                name = "",
                email = "",
                avatarUrl = null,
                joinDate = System.currentTimeMillis(),
                preferences = UserPreferences(
                    theme = "System",
                    notifications = true,
                    language = "English"
                )
            )
            _isLoading.value = false
        }
        // Observe todos from Room and update stats
        viewModelScope.launch {
            todoRepository.getAllTodos().collect { list ->
                _todos.value = list
                calculateUserStats()
            }
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


