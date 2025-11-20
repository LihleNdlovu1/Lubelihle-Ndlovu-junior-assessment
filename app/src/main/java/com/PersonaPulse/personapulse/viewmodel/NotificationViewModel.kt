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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val todoRepository: ITodoRepository
) : ViewModel() {
    
    val todos = todoRepository.getAllTodos()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        observeTodos()
    }
    
    private fun observeTodos() {
        viewModelScope.launch {
            todos.collect { todoList ->
                generateNotifications(todoList)
            }
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
    
    private fun generateNotifications(todoList: List<TodoData>) {
        val notificationList = mutableListOf<NotificationItem>()
        
        // Add notifications for due tasks (only incomplete tasks)
        todoList.filter { !it.isCompleted }.forEach { todo ->
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
        viewModelScope.launch {
            todos.collect { todoList ->
                generateNotifications(todoList)
            }
        }
    }
    
    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.filter { it.id != notificationId }
    }
    
    fun toggleTodoCompleted(todo: TodoData) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(
                    isCompleted = !todo.isCompleted,
                    completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
                )
                todoRepository.updateTodo(updatedTodo)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun deleteTodo(todo: TodoData) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
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



