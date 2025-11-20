package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTodoRepository : ITodoRepository {

    private val todos = mutableListOf<TodoData>()

    private val allTodosFlow = MutableStateFlow<List<TodoData>>(emptyList())

    var updatedTodo: TodoData? = null
    var shouldThrow = false

    private fun refreshFlows() {
        allTodosFlow.value = todos.toList()
    }

    suspend fun emitTodos(list: List<TodoData>) = allTodosFlow.emit(list)

    override fun getAllTodos(): Flow<List<TodoData>> = allTodosFlow

    override fun getIncompleteTodos(): Flow<List<TodoData>> =
        allTodosFlow.map { list -> list.filter { !it.isCompleted } }

    override fun getCompletedTodos(): Flow<List<TodoData>> =
        allTodosFlow.map { list -> list.filter { it.isCompleted } }

    override suspend fun getTodoById(id: String): TodoData? =
        todos.find { it.id == id }

    override suspend fun insertTodo(todo: TodoData) {
        todos.add(todo)
        refreshFlows()
    }

    override suspend fun updateTodo(todo: TodoData) {
        if (shouldThrow) throw Exception("Update failed")
        updatedTodo = todo

        val index = todos.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todos[index] = todo
            refreshFlows()
        }
    }

    override suspend fun deleteTodo(todo: TodoData) {
        todos.removeIf { it.id == todo.id }
        refreshFlows()
    }

    override suspend fun deleteTodoById(id: String) {
        todos.removeIf { it.id == id }
        refreshFlows()
    }

    override suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: Long?) {
        val index = todos.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = todos[index]
            todos[index] = old.copy(
                isCompleted = isCompleted,
                completedAt = completedAt
            )
            refreshFlows()
        }
    }

    override fun searchTodos(searchQuery: String): Flow<List<TodoData>> =
        allTodosFlow.map { list ->
            list.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }

    override fun getTodosByCategory(category: String): Flow<List<TodoData>> =
        allTodosFlow.map { list -> list.filter { it.category == category } }

    override fun getTodosByPriority(priority: Priority): Flow<List<TodoData>> =
        allTodosFlow.map { list -> list.filter { it.priority == priority } }

    override fun getTodosByDateRange(startTime: Long, endTime: Long): Flow<List<TodoData>> =
        allTodosFlow.map { list ->
            list.filter { it.timestamp in startTime..endTime }
        }

    override fun getIncompleteTodoCount(): Flow<Int> =
        allTodosFlow.map { list -> list.count { !it.isCompleted } }

    override fun getCompletedTodoCount(): Flow<Int> =
        allTodosFlow.map { list -> list.count { it.isCompleted } }

    override fun getTotalTodoCount(): Flow<Int> =
        allTodosFlow.map { it.size }
}
