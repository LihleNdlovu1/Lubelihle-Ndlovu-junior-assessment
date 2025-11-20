package com.PersonaPulse.personapulse.repository

import com.PersonaPulse.personapulse.database.dao.TodoDao
import com.PersonaPulse.personapulse.database.entity.TodoEntity
import com.PersonaPulse.personapulse.doubles.FakeTodoDao
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {
    private lateinit var todoDao: TodoDao
    private lateinit var repository: TodoRepository

    @Before
    fun setup(){
        todoDao = FakeTodoDao(
            listOf(
                TodoEntity(
                    id = "1",
                    title = "Test 1",
                    priority = Priority.LOW,
                    isCompleted = false,
                    category = "test"
                ),
                TodoEntity(
                    id = "2",
                    title = "Test 2",
                    priority = Priority.HIGH,
                    isCompleted = false,
                    category = "not test"
                ),
                TodoEntity(
                    id = "3",
                    title = "Test 3",
                    priority = Priority.MEDIUM,
                    isCompleted = true,
                    category = "test"
                )
            )
        )
        repository = TodoRepository(todoDao)

    }

    @Test
    fun testGetAll() = runBlocking {
        val todos = repository.getAllTodos().first()

        assertTrue(todos.isNotEmpty())
        assertTrue(todos.size == 3)
    }


    @Test
    fun testGetIncompleteTodos() = runBlocking {
        val todos = repository.getIncompleteTodos().first()

        assertTrue(todos.isNotEmpty())
        assertEquals(2, todos.size)

    }

    @Test
    fun testGetCompleteTodos() = runBlocking {
        val todos = repository.getCompletedTodos().first()

        assertTrue(todos.isNotEmpty())
        assertEquals(1, todos.size)
        assertEquals("3", todos.first().id)
    }

    @Test
    fun testAddTodo() = runBlocking {
        repository.insertTodo(TodoData(title = "add to test"))
        val todos = repository.getAllTodos().first()
        assertTrue(todos.size == 4)
        assertTrue(todos.any { it.title == "add to test" })
    }

    @Test
    fun testGetTodoById() = runBlocking {
        val todos = repository.getTodoById("3")
        assertTrue(todos?.title == "Test 3")
        assertFalse(todos?.title == "Test 1")

    }

    @Test
    fun testSearchTodos() = runBlocking {
        val todos = repository.searchTodos("Test 1").first()
        assertTrue(todos.isNotEmpty())
        assertTrue(todos.size == 1)
        assertTrue(todos[0].title == "Test 1")
    }

    @Test
    fun testDeleteTodo() = runBlocking {
        repository.deleteTodo(TodoData(title = "Test 1"))
        val todos = repository.getAllTodos().first()
        assertTrue(todos.size == 2)
        assertFalse(todos.contains(TodoData(title = "Test 1")))
    }

    @Test
    fun testDeleteTodoById() = runBlocking {
        repository.deleteTodoById("1")
        val todos = repository.getAllTodos().first()
        assertTrue(todos.size == 2)
        assertFalse(todos.contains(TodoData(title = "Test 1")))
    }

    @Test
    fun testUpdateTodo() = runBlocking {
        val todo = repository.getTodoById("1")
        if (todo != null) {
            repository.updateTodo(todo.copy(title = "Updated Test 1"))
            val updatedTodo = repository.getTodoById("1")
            assertTrue(updatedTodo?.title == "Updated Test 1")
        }
    }

    @Test
    fun testGetTodosByDateRange() = runBlocking {
        val todos = repository.getTodosByDateRange(startTime = 1763448793939, endTime = 1963448793939).first()
        assertTrue(todos.isNotEmpty())
        assertTrue(todos.size == 3)
    }

    @Test
    fun testGetTodosByPriority() = runBlocking {
        val todos = repository.getTodosByPriority(priority = Priority.LOW).first()
        assertTrue(todos.isNotEmpty())
        assertTrue(todos.size == 1)
        assertTrue(todos.all { it.priority == Priority.LOW })
    }

    @Test
    fun testGetTodosByCategory() = runBlocking {
        val todos = repository.getTodosByCategory("test").first()
        assertTrue(todos.isNotEmpty())
        assertTrue(todos.size == 2)
        assertTrue(todos.all { it.category == "test" })
    }

    @Test
    fun testGetIncompleteTodoCount() = runBlocking {
        val count = repository.getIncompleteTodoCount().first()
        assertTrue(count == 2)
    }

    @Test
    fun testGetCompletedTodoCount() = runBlocking {
        val count = repository.getCompletedTodoCount().first()
        assertTrue(count == 1)
    }

    @Test
    fun testGetTotalTodoCount() = runBlocking {
        val count = repository.getTotalTodoCount().first()
        assertTrue(count == 3)
    }

    @Test
    fun testUpdateTodoCompletion() = runBlocking {
        val todo = repository.getTodoById("1")
        val time = System.currentTimeMillis()
        repository.updateTodoCompletion(
            id = todo?.id ?: "",
            isCompleted = true,
            completedAt = time
        )

        val updatedTodo = repository.getTodoById("1")

        assertTrue(updatedTodo?.isCompleted == true)
        assertTrue(updatedTodo?.completedAt == time)

    }

}