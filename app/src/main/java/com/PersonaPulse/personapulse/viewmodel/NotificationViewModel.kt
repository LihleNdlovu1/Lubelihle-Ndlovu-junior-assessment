package com.PersonaPulse.personapulse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _todos = MutableStateFlow<List<TodoData>>(emptyList())
    val todos: StateFlow<List<TodoData>> = _todos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    init {
        loadMockData()
    }
    
    private fun loadMockData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Mock todo data with due dates
            val mockTodos = listOf(
                TodoData(
                    title = "Team meeting",
                    description = "Weekly standup meeting",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work",
                    dueDate = getTodayTimestamp() + 3600000 // 1 hour from now
                ),
                TodoData(
                    title = "Doctor appointment",
                    description = "Annual health checkup",
                    priority = com.PersonaPulse.personapulse.model.Priority.MEDIUM,
                    category = "Health",
                    dueDate = getTodayTimestamp() + 7200000 // 2 hours from now
                ),
                TodoData(
                    title = "Grocery shopping",
                    description = "Buy ingredients for dinner",
                    priority = com.PersonaPulse.personapulse.model.Priority.LOW,
                    category = "Personal",
                    dueDate = getTodayTimestamp() + 10800000 // 3 hours from now
                ),
                TodoData(
                    title = "Project deadline",
                    description = "Submit final project report",
                    priority = com.PersonaPulse.personapulse.model.Priority.HIGH,
                    category = "Work",
                    dueDate = getTodayTimestamp() - 3600000 // 1 hour ago (overdue)
                )
            )
            
            _todos.value = mockTodos
            generateNotifications()
            _isLoading.value = false
        }
    }
    
    private fun getTodayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun generateNotifications() {
        val notificationList = mutableListOf<NotificationItem>()
        
        // Add notifications for due tasks
        _todos.value.forEach { todo ->
            todo.dueDate?.let { dueDate ->
                val now = System.currentTimeMillis()
                val timeDiff = dueDate - now
                
                when {
                    timeDiff < 0 -> {
                        // Overdue
                        notificationList.add(
                            NotificationItem(
                                id = todo.id,
                                title = "Task Overdue",
                                message = "${todo.title} was due ${formatTimeAgo(-timeDiff)} ago",
                                type = NotificationType.OVERDUE,
                                timestamp = now
                            )
                        )
                    }
                    timeDiff < 3600000 -> {
                        // Due within 1 hour
                        notificationList.add(
                            NotificationItem(
                                id = todo.id,
                                title = "Task Due Soon",
                                message = "${todo.title} is due in ${formatTimeAgo(timeDiff)}",
                                type = NotificationType.DUE_SOON,
                                timestamp = now
                            )
                        )
                    }
                    timeDiff < 86400000 -> {
                        // Due today
                        notificationList.add(
                            NotificationItem(
                                id = todo.id,
                                title = "Task Due Today",
                                message = "${todo.title} is due today",
                                type = NotificationType.DUE_TODAY,
                                timestamp = now
                            )
                        )
                    }
                }
            }
        }
        
        _notifications.value = notificationList.sortedBy { it.timestamp }
    }
    
    private fun formatTimeAgo(timeDiff: Long): String {
        val hours = timeDiff / 3600000
        val minutes = (timeDiff % 3600000) / 60000
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "now"
        }
    }
    
    fun refreshNotifications() {
        generateNotifications()
    }
    
    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.filter { it.id != notificationId }
    }
    
    fun getTasksDueToday(): List<TodoData> {
        val today = getTodayTimestamp()
        val tomorrow = today + 86400000
        return _todos.value.filter { todo ->
            todo.dueDate?.let { dueDate ->
                dueDate in today until tomorrow
            } ?: false
        }
    }
    
    fun getOverdueTasks(): List<TodoData> {
        val now = System.currentTimeMillis()
        return _todos.value.filter { todo ->
            todo.dueDate?.let { dueDate ->
                dueDate < now && !todo.isCompleted
            } ?: false
        }
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long
)

enum class NotificationType {
    DUE_TODAY,
    DUE_SOON,
    OVERDUE
}
