package com.PersonaPulse.personapulse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

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

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {
    
    private val _performanceStats = MutableStateFlow<PerformanceStats?>(null)
    val performanceStats: StateFlow<PerformanceStats?> = _performanceStats.asStateFlow()
    
    private val _productivityInsights = MutableStateFlow<ProductivityInsights?>(null)
    val productivityInsights: StateFlow<ProductivityInsights?> = _productivityInsights.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _allTodos = MutableStateFlow<List<TodoData>>(emptyList())
    val allTodos: StateFlow<List<TodoData>> = _allTodos.asStateFlow()
    
    init {
        viewModelScope.launch {
            todoRepository.getAllTodos().collect { list ->
                _allTodos.value = list
                loadAnalytics()
            }
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
        val completedTodos = todos.filter { it.isCompleted && it.completedAt != null }
        
        // Calculate most productive day from actual completion data
        val mostProductiveDay = if (completedTodos.isNotEmpty()) {
            val dayOfWeekCounts = completedTodos.groupBy { todo ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = todo.completedAt!!
                calendar.get(Calendar.DAY_OF_WEEK)
            }.mapValues { it.value.size }
            
            val mostProductiveDayNum = dayOfWeekCounts.maxByOrNull { it.value }?.key
            when (mostProductiveDayNum) {
                Calendar.SUNDAY -> "Sunday"
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "No data yet"
            }
        } else {
            "No data yet"
        }
        
        // Calculate most productive time from actual completion data
        val mostProductiveTime = if (completedTodos.isNotEmpty()) {
            val hourCounts = completedTodos.groupBy { todo ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = todo.completedAt!!
                calendar.get(Calendar.HOUR_OF_DAY)
            }.mapValues { it.value.size }
            
            val mostProductiveHour = hourCounts.maxByOrNull { it.value }?.key
            if (mostProductiveHour != null) {
                val startHour = mostProductiveHour
                val endHour = (mostProductiveHour + 2) % 24
                val startPeriod = if (startHour < 12) "AM" else "PM"
                val endPeriod = if (endHour < 12) "AM" else "PM"
                val startHour12 = if (startHour == 0) 12 else if (startHour > 12) startHour - 12 else startHour
                val endHour12 = if (endHour == 0) 12 else if (endHour > 12) endHour - 12 else endHour
                "$startHour12:00 $startPeriod - $endHour12:00 $endPeriod"
            } else {
                "No data yet"
            }
        } else {
            "No data yet"
        }
        
        // Calculate top category from actual data
        val categoryCounts = completedTodos.groupBy { it.category ?: "General" }
            .mapValues { it.value.size }
        val topCategory = if (categoryCounts.isNotEmpty()) {
            categoryCounts.maxByOrNull { it.value }?.key ?: "General"
        } else {
            "No data yet"
        }
        
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


