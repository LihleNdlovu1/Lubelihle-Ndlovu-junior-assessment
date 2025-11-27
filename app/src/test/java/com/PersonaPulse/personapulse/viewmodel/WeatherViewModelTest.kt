package com.PersonaPulse.personapulse.viewmodel

import com.PersonaPulse.personapulse.MainDispatcherRule
import com.PersonaPulse.personapulse.doubles.FakeGeocodingService
import com.PersonaPulse.personapulse.doubles.FakeLocationManager
import com.PersonaPulse.personapulse.doubles.FakeWeatherService
import com.PersonaPulse.personapulse.model.CurrentWeather
import com.PersonaPulse.personapulse.model.WeatherResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import kotlin.random.Random
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var fakeWeatherService: FakeWeatherService
    private lateinit var fakeLocationManager: FakeLocationManager
    private lateinit var fakeGeocodingService: FakeGeocodingService
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        fakeWeatherService = FakeWeatherService()
        fakeLocationManager = FakeLocationManager()
        fakeGeocodingService = FakeGeocodingService()
        viewModel = WeatherViewModel(
            weatherService = fakeWeatherService,
            locationManager = fakeLocationManager,
            geocodingService = fakeGeocodingService)
    }

    @Test
    fun `fetchWeather loads fake weather successfully`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))


        viewModel.fetchWeather()

        assertEquals(fakeWeatherService.fakeResponse, viewModel.weather.value)

    }

    @Test
    fun `fetchWeatherForCurrentLocation loads fake weather successfully`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))

        viewModel.fetchWeatherForCurrentLocation()

        assertEquals(fakeWeatherService.fakeResponse, viewModel.weather.value)
    }

    @Test
    fun `hasLocationPermission() returns true when location permission is granted`() = runTest {
        fakeLocationManager.hasPermission = true
        assertTrue(viewModel.hasLocationPermission())
    }

    @Test
    fun `hasLocationPermission() returns false when location permission is not granted`() = runTest {
        fakeLocationManager.hasPermission = false
        assertFalse(viewModel.hasLocationPermission())
    }

    @Test
    fun `refreshWeather() fetches new weather data`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))

        viewModel.refreshWeather()

        assertEquals(fakeWeatherService.fakeResponse, viewModel.weather.value)
    }

    @Test
    fun `getWeatherIcon() returns correct icon for weather code`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))

        viewModel.fetchWeather()
        val icon = viewModel.getWeatherIcon()
        when (weatherCode) {
            0 -> assertEquals("☀️", icon)
            1 -> assertEquals("⛅", icon)
            2 -> assertEquals("☁️", icon)
        }
    }

    @Test
    fun `getTemperatureColor() returns correct color for temperature`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))

        viewModel.fetchWeather()
        val tempColor = viewModel.getTemperatureColor()
        when{
            temperature < 0 -> assertEquals("#87CEEB", tempColor)
            temperature < 10 -> assertEquals("#ADD8E6", tempColor)
            temperature < 20 -> assertEquals("#90EE90", tempColor)
            temperature < 25 -> assertEquals("#FFD700", tempColor)
            temperature < 30 -> assertEquals("#FFA500", tempColor)
            else -> assertEquals("#FF6347", tempColor)
        }
    }

    @Test
    fun `updateCity returns correct city`() = runTest {
        val temperature = Random.nextInt(-5, 35)
        val weatherCode = Random.nextInt(0, 3)
        fakeWeatherService.fakeResponse = WeatherResponse(
            latitude = -26.2041,
            longitude = 28.0473,
            generationtime_ms = 0.0,
            utc_offset_seconds = 7200,
            timezone = "Africa/Johannesburg",
            timezone_abbreviation = "SAST",
            elevation = 1753.0,
            current_weather = CurrentWeather(
                temperature = temperature.toDouble(),
                windspeed = Random.nextDouble(5.0, 25.0),
                winddirection = Random.nextDouble(0.0, 360.0),
                weathercode = weatherCode,
                is_day = 1,
                time = System.currentTimeMillis().toString()
            ))

        viewModel.fetchWeather()
        viewModel.updateCity("Sandton")
        assertEquals("Sandton", viewModel.selectedCity.value)
    }
}