package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeTodoRepository
import com.PersonaPulse.personapulse.model.TodoData
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {
    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var fakeRepo: FakeTodoRepository

    @Before
    fun setUp() {
        fakeRepo = FakeTodoRepository()
        viewModel = AnalyticsViewModel(fakeRepo)
    }


    @Test
    fun `completed tasks are calculated correctly`() = runTest {
        val task1 = TodoData(id = "1", title = "A", isCompleted = false)
        val task2 = TodoData(id = "2", title = "B", isCompleted = true, completedAt = 2000L, timestamp = 1000L)

        fakeRepo.insertTodo(task1)
        fakeRepo.insertTodo(task2)

        advanceUntilIdle()

        val stats = viewModel.performanceStats.value!!

        assertEquals(1, stats.completedTasks)
        assertEquals(2, stats.totalTasks)
        assertEquals(0.5f, stats.completionRate)
    }

}