package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _todos = MutableStateFlow<List<TodoData>>(emptyList())
    val todos: StateFlow<List<TodoData>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadMockData()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Mock completed todo data for history
            val mockCompletedTodos = listOf(
                TodoData(
                    title = "Complete project proposal",
                    description = "Draft and review the Q4 project proposal",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 * 2 // 2 days ago
                ),
                TodoData(
                    title = "Grocery shopping",
                    description = "Buy ingredients for weekend cooking",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Personal",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 // 1 day ago
                ),
                TodoData(
                    title = "Call dentist",
                    description = "Schedule annual checkup",
                    priority = com.PersonaPulse.personapulse.model.Priority.LOW,
                    category = "Health",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 * 3 // 3 days ago
                ),
                TodoData(
                    title = "Read book chapter",
                    description = "Finish chapter 5 of 'Clean Code'",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Learning",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 * 5 // 5 days ago
                )
            )
            
            _todos.value = mockCompletedTodos
            _isLoading.value = false
        }
    }
    
    fun refreshHistory() {
        loadMockData()
    }
    
    fun getCompletedTodos(): List<TodoData> {
        return _todos.value.filter { it.isCompleted }
    }
    
    fun getTodosByDateRange(startDate: Long, endDate: Long): List<TodoData> {
        return _todos.value.filter { todo ->
            todo.completedAt?.let { completedAt ->
                completedAt in startDate..endDate
            } ?: false
        }
    }
}
