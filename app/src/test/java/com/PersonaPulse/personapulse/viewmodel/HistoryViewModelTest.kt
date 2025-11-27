package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeTodoRepository
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HistoryViewModel
    private lateinit var fakeRepo: FakeTodoRepository

    @Before
    fun setUp() {
        fakeRepo = FakeTodoRepository()
        viewModel = HistoryViewModel(fakeRepo)
    }

    @Test
    fun `getCompletedTodos only returns completed tasks`() = runTest{
        val task1 = TodoData(id = "1", title = "Task 1", isCompleted = false)
        val task2 = TodoData(id = "2", title = "Task 2", isCompleted = true)

        fakeRepo.insertTodo(todo = task1)
        fakeRepo.insertTodo(todo = task2)

        advanceUntilIdle()

        val result = viewModel.getCompletedTodos()

        assertEquals(1, result.size)
        assertTrue(result.contains(task2))
        assertFalse(result.contains(task1))
    }

    @Test
    fun `getTodosByDateRange returns tasks for a specific date`() = runTest {
        val task1 = TodoData(id = "1", title = "Task 1", isCompleted = false)
        val task2 = TodoData(id = "2", title = "Task 2", isCompleted = true, completedAt = 1234567890)

        fakeRepo.insertTodo(todo = task1)
        fakeRepo.insertTodo(todo = task2)

        advanceUntilIdle()

        val result = viewModel.getTodosByDateRange(startDate = 1234567890, endDate = 1234567890 )

        assertEquals(1, result.size)
        assertTrue(result.contains(task2))
    }

    @Test
    fun `toggleTodo marks task as incompleted`() = runTest {
        val original = TodoData(
            id = "123",
            title = "Test",
            isCompleted = true,
            completedAt = 999999L
        )

        viewModel.toggleTodo(original)
        advanceUntilIdle()

        val updated = fakeRepo.updatedTodo
        assertNotNull(updated)


        assertFalse(updated!!.isCompleted)

        assertNull(updated.completedAt)
    }



}