package com.PersonaPulse.personapulse.repository

import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun getAllTodos(): Flow<List<TodoData>>
    fun getIncompleteTodos(): Flow<List<TodoData>>
    fun getCompletedTodos(): Flow<List<TodoData>>

    suspend fun getTodoById(id: String): TodoData?
    suspend fun insertTodo(todo: TodoData)
    suspend fun updateTodo(todo: TodoData)
    suspend fun deleteTodo(todo: TodoData)
    suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: Long?)

    suspend fun deleteTodoById(id: String)

    fun searchTodos(searchQuery: String): Flow<List<TodoData>>
    fun getTodosByCategory(category: String): Flow<List<TodoData>>
    fun getTodosByPriority(priority: Priority): Flow<List<TodoData>>
    fun getTodosByDateRange(startTime: Long, endTime: Long): Flow<List<TodoData>>

    fun getIncompleteTodoCount(): Flow<Int>
    fun getCompletedTodoCount(): Flow<Int>
    fun getTotalTodoCount(): Flow<Int>
}