package com.PersonaPulse.personapulse.repository

import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.database.entity.TodoEntity
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
): ITodoRepository {
    override fun getAllTodos(): Flow<List<TodoData>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override fun getIncompleteTodos(): Flow<List<TodoData>> {
        return todoDao.getIncompleteTodos().map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override fun getCompletedTodos(): Flow<List<TodoData>> {
        return todoDao.getCompletedTodos().map { entities ->
            entities.map { it.toTodoData() }
        }
    }


    override suspend fun getTodoById(id: String): TodoData? {
        return todoDao.getTodoById(id)?.toTodoData()
    }

    override fun getTodosByDateRange(startTime: Long, endTime: Long): Flow<List<TodoData>> {
        return todoDao.getTodosByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override fun getTodosByPriority(priority: Priority): Flow<List<TodoData>> {
        return todoDao.getTodosByPriority(priority).map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override fun getTodosByCategory(category: String): Flow<List<TodoData>> {
        return todoDao.getTodosByCategory(category).map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override fun searchTodos(searchQuery: String): Flow<List<TodoData>> {
        return todoDao.searchTodos("%$searchQuery%").map { entities ->
            entities.map { it.toTodoData() }
        }
    }

    override suspend fun insertTodo(todo: TodoData) {
        todoDao.insertTodo(todo.toTodoEntity())
    }

    /*
    suspend fun insertTodos(todos: List<TodoData>) {
        todoDao.insertTodos(todos.map { it.toTodoEntity() })
    }
     */

    override suspend fun updateTodo(todo: TodoData) {
        todoDao.updateTodo(todo.toTodoEntity())
    }

    override suspend fun deleteTodo(todo: TodoData) {
        todoDao.deleteTodo(todo.toTodoEntity())
    }

    override suspend fun deleteTodoById(id: String) {
        todoDao.deleteTodoById(id)
    }

    /*
    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }

     */

    override suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: Long?) {
        todoDao.updateTodoCompletion(id, isCompleted, completedAt)
    }

    override fun getIncompleteTodoCount(): Flow<Int> {
        return todoDao.getIncompleteTodoCount()
    }

    override fun getCompletedTodoCount(): Flow<Int> {
        return todoDao.getCompletedTodoCount()
    }

    override fun getTotalTodoCount(): Flow<Int> {
        return todoDao.getTotalTodoCount()
    }


}

// Extension functions to convert between TodoData and TodoEntity
fun TodoEntity.toTodoData(): TodoData {
    return TodoData(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        timestamp = this.timestamp,
        completedAt = this.completedAt,
        dueDate = this.dueDate,
        reminderTime = this.reminderTime,
        category = this.category,
        priority = this.priority,
        recurrence = this.recurrence
    )
}

fun TodoData.toTodoEntity(): TodoEntity {
    return TodoEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        timestamp = this.timestamp,
        completedAt = this.completedAt,
        dueDate = this.dueDate,
        reminderTime = this.reminderTime,
        category = this.category,
        priority = this.priority,
        recurrence = this.recurrence
    )
}
