package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.PersonaPulse.personapulse.model.TodoData
import kotlin.math.roundToInt

data class PerformanceStats(
    val completedTasks: Int,
    val totalTasks: Int,
    val completionRate: Float,
    val averageCompletionTime: Float,
    val highPriorityCompleted: Int,
    val productivityScore: Int
)

data class ProductivityInsights(
    val mostProductiveDay: String,
    val mostProductiveTime: String,
    val topCategory: String,
    val improvementSuggestions: List<String>
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _performanceStats = MutableStateFlow<PerformanceStats?>(null)
    val performanceStats: StateFlow<PerformanceStats?> = _performanceStats.asStateFlow()
    
    private val _productivityInsights = MutableStateFlow<ProductivityInsights?>(null)
    val productivityInsights: StateFlow<ProductivityInsights?> = _productivityInsights.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Mock data for now - in a real app, this would come from a repository
    private val _allTodos = MutableStateFlow<List<TodoData>>(emptyList())
    val allTodos: StateFlow<List<TodoData>> = _allTodos.asStateFlow()
    
    init {
        loadMockData()
        loadAnalytics()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            val mockTodos = listOf(
                TodoData(
                    title = "Complete project proposal",
                    description = "Draft and review the Q4 project proposal",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 86400000 // 1 day ago
                ),
                TodoData(
                    title = "Grocery shopping",
                    description = "Buy ingredients for weekend cooking",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Personal",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 172800000 // 2 days ago
                ),
                TodoData(
                    title = "Call dentist",
                    description = "Schedule annual checkup",
                    priority = com.PersonaPulse.personapulse.model.Priority.LOW,
                    category = "Health",
                    isCompleted = false
                ),
                TodoData(
                    title = "Read technical documentation",
                    description = "Study new framework documentation",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Learning",
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 259200000 // 3 days ago
                ),
                TodoData(
                    title = "Exercise routine",
                    description = "Complete 30-minute workout",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Health",
                    isCompleted = false
                )
            )
            _allTodos.value = mockTodos
        }
    }
    
    fun refreshAnalytics() {
        loadAnalytics()
    }
    
    private fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val todos = _allTodos.value
                val stats = calculatePerformanceStats(todos)
                val insights = generateProductivityInsights(todos, stats)
                
                _performanceStats.value = stats
                _productivityInsights.value = insights
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun calculatePerformanceStats(todos: List<TodoData>): PerformanceStats {
        val completedTasks = todos.count { it.isCompleted }
        val totalTasks = todos.size
        val completionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
        
        val completedTodos = todos.filter { it.isCompleted }
        val averageCompletionTime = if (completedTodos.isNotEmpty()) {
            completedTodos.mapNotNull { todo ->
                todo.completedAt?.let { completedAt ->
                    completedAt - todo.timestamp
                }
            }.average().toFloat()
        } else 0f
        
        val highPriorityCompleted = completedTodos.count { 
            it.priority == com.PersonaPulse.personapulse.model.Priority.HIGH 
        }
        
        val productivityScore = calculateProductivityScore(completionRate, highPriorityCompleted, totalTasks)
        
        return PerformanceStats(
            completedTasks = completedTasks,
            totalTasks = totalTasks,
            completionRate = completionRate,
            averageCompletionTime = averageCompletionTime,
            highPriorityCompleted = highPriorityCompleted,
            productivityScore = productivityScore
        )
    }
    
    private fun calculateProductivityScore(
        completionRate: Float, 
        highPriorityCompleted: Int, 
        totalTasks: Int
    ): Int {
        val baseScore = (completionRate * 50).roundToInt()
        val priorityBonus = (highPriorityCompleted * 10).coerceAtMost(30)
        val consistencyBonus = if (completionRate > 0.8f) 20 else 0
        
        return (baseScore + priorityBonus + consistencyBonus).coerceIn(0, 100)
    }
    
    private fun generateProductivityInsights(
        todos: List<TodoData>, 
        stats: PerformanceStats
    ): ProductivityInsights {
        val completedTodos = todos.filter { it.isCompleted }
        
        // Mock insights - in a real app, this would analyze actual data patterns
        val mostProductiveDay = "Tuesday"
        val mostProductiveTime = "10:00 AM - 12:00 PM"
        
        val categoryCounts = completedTodos.groupBy { it.category ?: "Uncategorized" }
            .mapValues { it.value.size }
        val topCategory = categoryCounts.maxByOrNull { it.value }?.key ?: "Work"
        
        val suggestions = mutableListOf<String>()
        
        when {
            stats.completionRate < 0.5f -> {
                suggestions.add("Try breaking down large tasks into smaller, manageable chunks")
                suggestions.add("Set specific deadlines for your tasks")
            }
            stats.completionRate < 0.8f -> {
                suggestions.add("Great progress! Try to maintain consistency in your daily routine")
                suggestions.add("Consider using time-blocking techniques")
            }
            else -> {
                suggestions.add("Excellent productivity! Keep up the great work")
                suggestions.add("Consider taking on more challenging tasks")
            }
        }
        
        if (stats.highPriorityCompleted < 2) {
            suggestions.add("Focus more on high-priority tasks to improve overall productivity")
        }
        
        return ProductivityInsights(
            mostProductiveDay = mostProductiveDay,
            mostProductiveTime = mostProductiveTime,
            topCategory = topCategory,
            improvementSuggestions = suggestions
        )
    }
}
