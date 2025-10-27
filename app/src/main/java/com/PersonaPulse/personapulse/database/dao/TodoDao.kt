package com.PersonaPulse.personapulse.database.dao

import androidx.room.*
import com.PersonaPulse.personapulse.database.entity.TodoEntity
import com.PersonaPulse.personapulse.model.Priority
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY timestamp DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY timestamp DESC")
    fun getIncompleteTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: String): TodoEntity?

    @Query("SELECT * FROM todos WHERE dueDate IS NOT NULL AND dueDate >= :startTime AND dueDate <= :endTime ORDER BY dueDate ASC")
    fun getTodosByDateRange(startTime: Long, endTime: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY timestamp DESC")
    fun getTodosByPriority(priority: Priority): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE category = :category ORDER BY timestamp DESC")
    fun getTodosByCategory(category: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY timestamp DESC")
    fun searchTodos(searchQuery: String): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: String)

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()

    @Query("UPDATE todos SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 0")
    fun getIncompleteTodoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 1")
    fun getCompletedTodoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos")
    fun getTotalTodoCount(): Flow<Int>
}



