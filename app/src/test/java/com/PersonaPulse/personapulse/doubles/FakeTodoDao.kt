package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.database.entity.TodoEntity
import com.PersonaPulse.personapulse.model.Priority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTodoDao(
    seed: List<TodoEntity> = emptyList<TodoEntity>()
): TodoDao {
    private val backing = seed.toMutableList()

    override fun getAllTodos(): Flow<List<TodoEntity>> {
       return flowOf(backing)
    }

    override fun getIncompleteTodos(): Flow<List<TodoEntity>> {
        return flowOf(backing.filter { !it.isCompleted })
    }

    override fun getCompletedTodos(): Flow<List<TodoEntity>> {
        return flowOf(backing.filter { it.isCompleted })
    }

    override suspend fun getTodoById(id: String): TodoEntity? {
        return backing.find { it.id == id }
    }

    override fun getTodosByDateRange(
        startTime: Long,
        endTime: Long
    ): Flow<List<TodoEntity>> {
        return flowOf(
            backing.filter { it.timestamp in startTime..endTime }
        )
    }

    override fun getTodosByPriority(priority: Priority): Flow<List<TodoEntity>> {
        return flowOf(
            backing.filter { it.priority == priority }
        )
    }

    override fun getTodosByCategory(category: String): Flow<List<TodoEntity>> {
        return flowOf(
            backing.filter { it.category == category }
        )
    }

    override fun searchTodos(searchQuery: String): Flow<List<TodoEntity>> {
        val cleanQuery = searchQuery.replace("%", "", ignoreCase = true)
        return flowOf(
            backing.filter { todo ->
                todo.title.contains(cleanQuery, ignoreCase = true)
            }
        )
    }

    override suspend fun insertTodo(todo: TodoEntity) {
        backing.add(todo)
    }


    override suspend fun updateTodo(todo: TodoEntity) {
        val index = backing.indexOfFirst { it.id == todo.id }
        backing[index] = todo
    }

    override suspend fun deleteTodo(todo: TodoEntity) {
        backing.removeIf { it.title == todo.title }
    }

    override suspend fun deleteTodoById(id: String) {
        backing.removeIf { it.id == id }
    }

    override suspend fun updateTodoCompletion(
        id: String,
        isCompleted: Boolean,
        completedAt: Long?
    ) {
        val index = backing.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = backing[index]
            backing[index] = existing.copy(
                isCompleted = isCompleted,
                completedAt = completedAt
            )
        }
    }

    override fun getIncompleteTodoCount(): Flow<Int> {
        return flowOf(
            backing.filter { !it.isCompleted }.size
        )
    }

    override fun getCompletedTodoCount(): Flow<Int> {
        return flowOf(
            backing.filter { it.isCompleted }.size
        )
    }

    override fun getTotalTodoCount(): Flow<Int> {
        return flowOf(
            backing.size
        )
    }

}