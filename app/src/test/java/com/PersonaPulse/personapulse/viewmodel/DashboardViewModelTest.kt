package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeGeocodingService
import com.PersonaPulse.personapulse.doubles.FakeLocationManager
import com.PersonaPulse.personapulse.doubles.FakeNotificationManager
import com.PersonaPulse.personapulse.doubles.FakeTodoRepository
import com.PersonaPulse.personapulse.doubles.FakeWeatherService
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.network.GeocodeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel
    private lateinit var fakeRepo: FakeTodoRepository
    private lateinit var fakeNotificationManager: FakeNotificationManager
    private lateinit var fakeWeatherService: FakeWeatherService
    private lateinit var fakeGeocodingService: FakeGeocodingService
    private lateinit var fakeLocationManager: FakeLocationManager

    @Before
    fun setUp() {
        fakeRepo = FakeTodoRepository()
        fakeNotificationManager = FakeNotificationManager()
        fakeWeatherService = FakeWeatherService()
        fakeGeocodingService = FakeGeocodingService()
        fakeLocationManager = FakeLocationManager()
        viewModel = DashboardViewModel(
            todoRepository = fakeRepo,
            notificationManager = fakeNotificationManager,
            weatherService = fakeWeatherService,
            geocodingService = fakeGeocodingService,
            locationManager = fakeLocationManager
        )
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
    fun `clearErrorMessage clears error message`() = runTest {
        viewModel.run {
            val errorField = this::class.java.getDeclaredField("_errorMessage")
            errorField.isAccessible = true
            val stateFlow = errorField.get(this) as MutableStateFlow<String?>
            stateFlow.value = "Some error"
        }
        viewModel.clearErrorMessage()

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `clearSuccessMessage sets successMessage to null`() = runTest {
        viewModel.run {
            val successField = this::class.java.getDeclaredField("_successMessage")
            successField.isAccessible = true
            val stateFlow = successField.get(this) as MutableStateFlow<String?>
            stateFlow.value = "Success!"
        }

        viewModel.clearSuccessMessage()

        assertNull(viewModel.successMessage.value)
    }


    @Test
    fun `deleteTodo deletes todo`() = runTest {
        val original = TodoData(
            id = "123",
            title = "Test",
            isCompleted = false,
            completedAt = null
        )

        fakeRepo.insertTodo(original)
        viewModel.deleteTodo(original)
        advanceUntilIdle()

        val todos = fakeRepo.getAllTodos().first()
        assertEquals(0, todos.size)
    }

    @Test
    fun `addTodo creates todo`() = runTest {
        viewModel.addTodo(title = "Test", description = "its just a test", priority = Priority.MEDIUM, dueDate = System.currentTimeMillis() + 3600000)
        advanceUntilIdle()

        val todos = fakeRepo.getAllTodos().first()
        assertEquals(1, todos.size)
        assertEquals("Test", todos[0].title)
        assertEquals("its just a test", todos[0].description)
        assertEquals(Priority.MEDIUM, todos[0].priority)
    }

    @Test
    fun `updateTodo updates todo`() = runTest {
        val original = TodoData(
            id = "123",
            title = "Test 1",
            isCompleted = false,
            completedAt = null
        )
        fakeRepo.insertTodo(original)

        viewModel.updateTodo(todo = original, title = "Test", description = "its just a test", priority = Priority.MEDIUM, dueDate = System.currentTimeMillis() + 3600000)
        advanceUntilIdle()

        val todos = fakeRepo.getAllTodos().first()
        assertEquals(1, todos.size)
        assertEquals("Test", todos[0].title)
        assertEquals("its just a test", todos[0].description)
    }

    @Test
    fun `fetchWeather - success - loads weather and clears errors`() = runTest {
        fakeGeocodingService.results = listOf(
            GeocodeResult(
                display_name = "Pretoria", lat = "10.0", lon = "20.0"
            )
        )

        viewModel.fetchWeather("Pretoria")
        advanceUntilIdle()

        assertEquals("Pretoria", viewModel.selectedCity.value)
    }

    @Test
    fun `fetchWeather - city not found - sets error`() = runTest {
        fakeGeocodingService.results = emptyList()

        viewModel.fetchWeather("Atlantis")
        advanceUntilIdle()

        assertEquals("City not found", viewModel.weatherError.value)
    }

    @Test
    fun `fetchWeather - API fails - fallback to mock weather`() = runTest {
        fakeGeocodingService.results = listOf(
            GeocodeResult("Pretoria", "1.0", "2.0")
        )
        fakeWeatherService.shouldThrow = true

        viewModel.fetchWeather("Pretoria")
       advanceUntilIdle()

        assertNotNull(viewModel.weather.value)
        assertEquals("Pretoria", viewModel.selectedCity.value)
        assertTrue(viewModel.weatherError.value!!.contains("Failed to fetch weather"))
    }

}