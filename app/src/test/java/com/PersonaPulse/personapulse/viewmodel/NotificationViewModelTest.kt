package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeTodoRepository
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NotificationViewModel
    private lateinit var fakeRepo: FakeTodoRepository

    @Before
    fun setUp() {
        fakeRepo = FakeTodoRepository()
        viewModel = NotificationViewModel(fakeRepo)
    }

    @Test
    fun `overdue task generates overdue notification`() = runTest{
        val overdueTask = TodoData(
            id = "1",
            title = "Overdue Task",
            dueDate = System.currentTimeMillis() - 3500000,
            isCompleted = false
        )

        fakeRepo.emitTodos(listOf(overdueTask))

        advanceUntilIdle()

        val notifications = viewModel.notifications.value
        assertEquals(1, notifications.size)
        assertEquals(NotificationType.OVERDUE, notifications[0].type)
        assertEquals("1", notifications[0].id)
    }

    @Test
    fun `task due within 1 hour generates DUE_SOON notification`() = runTest {
        val dueSoonTodo = TodoData(
            id = "2",
            title = "Finish assignment",
            description = "",
            dueDate = System.currentTimeMillis() + 30 * 60 * 1000, // 30 minutes from now
            isCompleted = false
        )

        fakeRepo.emitTodos(listOf(dueSoonTodo))

        advanceUntilIdle()

        val notifications = viewModel.notifications.value
        assertEquals(1, notifications.size)
        assertEquals(NotificationType.DUE_SOON, notifications[0].type)
        assertEquals("2", notifications[0].id)
    }

    @Test
    fun `task due later today generates DUE_TODAY notification`() = runTest {
        val dueTodayTodo = TodoData(
            id = "3",
            title = "Team meeting",
            description = "",
            dueDate = System.currentTimeMillis() + 5 * 60 * 60 * 1000, // 5 hours from now
            isCompleted = false
        )

        fakeRepo.emitTodos(listOf(dueTodayTodo))

        advanceUntilIdle()

        val notifications = viewModel.notifications.value
        assertEquals(1, notifications.size)
        assertEquals(NotificationType.DUE_TODAY, notifications[0].type)
        assertEquals("3", notifications[0].id)
    }

    @Test
    fun `refreshNotifications updates notifications`() = runTest {
        val todo = TodoData(
            id = "1",
            title = "Test",
            dueDate = System.currentTimeMillis() + 1000,
            isCompleted = false
        )
        fakeRepo.emitTodos(listOf(todo))

        viewModel.refreshNotifications()
        advanceUntilIdle()

        val notifications = viewModel.notifications.value
        assertEquals(1, notifications.size)
    }

    @Test
    fun `mark notification as read updates notification`() = runTest {
        val todo = TodoData(
            id = "1",
            title = "Test",
            dueDate = System.currentTimeMillis() + 1000,
            isCompleted = false
        )
        fakeRepo.emitTodos(listOf(todo))

        viewModel.refreshNotifications()
        advanceUntilIdle()

        viewModel.markNotificationAsRead("1")
        advanceUntilIdle()

        val notifications = viewModel.notifications.value
        assertEquals(0, notifications.size)
        assertTrue(notifications.isEmpty())
    }

    @Test
    fun `deleteTodo deletes todo`() = runTest {
        val todo = TodoData(
            id = "1",
            title = "Test",
        )
        fakeRepo.emitTodos(listOf(todo))

        viewModel.deleteTodo(todo)
        advanceUntilIdle()

        val todos = fakeRepo.getAllTodos().first()
        assertEquals(0, todos.size)
    }

    @Test
    fun `toggleTodoCompleted marks todo as completed`() = runTest {
        val original = TodoData(
            id = "123",
            title = "Test",
            isCompleted = false,
            completedAt = null
        )

        viewModel.toggleTodoCompleted(original)
        advanceUntilIdle()

        val updated = fakeRepo.updatedTodo
        assertNotNull(updated)

        assertTrue(updated!!.isCompleted)

        assertNotNull(updated.completedAt)
    }

    @Test
    fun `toggleTodoCompleted marks todo as uncompleted`() = runTest {

        val original = TodoData(
            id = "123",
            title = "Test",
            isCompleted = true,
            completedAt = 999999L
        )

        viewModel.toggleTodoCompleted(original)
        advanceUntilIdle()

        val updated = fakeRepo.updatedTodo
        assertNotNull(updated)


        assertFalse(updated!!.isCompleted)

        assertNull(updated.completedAt)
    }

}