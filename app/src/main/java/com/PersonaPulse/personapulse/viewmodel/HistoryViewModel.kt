package com.PersonaPulse.personapulse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {
    
    // Observe completed todos from Room
    val todos: StateFlow<List<TodoData>> = todoRepository.getCompletedTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    
    fun refreshHistory() {
        // No-op for Room Flow; kept for API symmetry
    }
    
    fun getCompletedTodos(): List<TodoData> {
        return todos.value
    }
    
    fun getTodosByDateRange(startDate: Long, endDate: Long): List<TodoData> {
        return todos.value.filter { todo ->
            todo.completedAt?.let { completedAt ->
                completedAt in startDate..endDate
            } ?: false
        }
    }
    
    fun toggleTodo(todo: TodoData) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(
                isCompleted = !todo.isCompleted,
                completedAt = if (!todo.isCompleted) System.currentTimeMillis() else null
            )
            todoRepository.updateTodo(updatedTodo)
        }
    }
}


