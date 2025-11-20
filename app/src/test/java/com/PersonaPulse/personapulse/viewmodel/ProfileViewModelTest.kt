package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeTodoRepository
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var fakeRepo: FakeTodoRepository

    @Before
    fun setUp() {
        fakeRepo = FakeTodoRepository()
        viewModel = ProfileViewModel(todoRepository = fakeRepo)
    }

    @Test
    fun `initial state loads default user profile`() = runTest {
        val profile = viewModel.userProfile.first { it != null }
        assertEquals("local_user", profile?.id)
        assertEquals("", profile?.name)
        assertEquals("", profile?.email)
    }

    @Test
    fun `initial todos list is empty`() = runTest {
        val todos = viewModel.todos.first()
        assertTrue(todos.isEmpty())
    }

    @Test
    fun `stats update when todos change`() = runTest {
        // Add some todos
        fakeRepo.insertTodo(
            TodoData(
                id = "1",
                title = "A",
                isCompleted = true,
                priority = Priority.HIGH
            )
        )

        fakeRepo.insertTodo(
            TodoData(
                id = "2",
                title = "B",
                isCompleted = false,
                priority = Priority.MEDIUM
            )
        )

        // Wait for flows to push
        advanceUntilIdle()

        val stats = viewModel.userStats.value!!

        assertEquals(2, stats.totalTasks)
        assertEquals(1, stats.completedTasks)
        assertEquals(1, stats.pendingTasks)

        // Priority-based checks
        assertEquals(1, stats.highPriorityCompleted)
        assertEquals(0, stats.mediumPriorityCompleted)
        assertEquals(0, stats.lowPriorityCompleted)

        // Completion rate = 1/2
        assertEquals(0.5f, stats.completionRate)
    }

    @Test
    fun `updateProfile changes user profile`() = runTest {
        viewModel.updateProfile("Felix", "felix@example.com")
        advanceUntilIdle()

        val profile = viewModel.userProfile.value!!
        assertEquals("Felix", profile.name)
        assertEquals("felix@example.com", profile.email)
    }

    @Test
    fun `updatePreferences updates user preferences`() = runTest {
        val newPrefs = UserPreferences(
            theme = "Dark",
            notifications = false,
            language = "Zulu"
        )

        viewModel.updatePreferences(newPrefs)
        advanceUntilIdle()

        val profile = viewModel.userProfile.value!!
        assertEquals("Dark", profile.preferences.theme)
        assertFalse(profile.preferences.notifications)
        assertEquals("Zulu", profile.preferences.language)
    }

}