package com.example.personapulse.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.personapulse.model.TodoData
import com.example.personapulse.model.WeatherResponse
import com.example.personapulse.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedCity = MutableStateFlow("Johannesburg")
    val selectedCity: StateFlow<String> = _selectedCity

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather


    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading

    private val _selectedCategory = MutableStateFlow("top")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _goals = MutableStateFlow<List<TodoData>>(emptyList())
    val goals: StateFlow<List<TodoData>> = _goals

    private val fileName = "goals.json"
    private val file = File(application.filesDir, fileName)

    init {
        loadGoals()
    }

    fun setCity(city: String) {
        _selectedCity.value = city
        fetchWeather()
    }

    fun addGoal(title: String, dueDate: Long? = null) {
        val updated = _goals.value + TodoData(title = title, dueDate = dueDate)
        _goals.value = updated
        saveGoals(updated)
    }

    fun updateGoal(index: Int, title: String, dueDate: Long?) {
        val updated = _goals.value.toMutableList().apply {
            this[index] = this[index].copy(title = title, dueDate = dueDate)
        }
        _goals.value = updated
        saveGoals(updated)
    }

    fun toggleGoalCompleted(index: Int) {
        val updated = _goals.value.toMutableList().apply {
            this[index] = this[index].copy(isCompleted = !this[index].isCompleted)
        }
        _goals.value = updated
        saveGoals(updated)
    }

    fun fetchWeather() {
        viewModelScope.launch {
            try {
                _isWeatherLoading.value = true
                _weatherError.value = null

                val geo = ApiClient.geocodingService.searchCity(_selectedCity.value).firstOrNull()
                if (geo != null) {
                    val weatherResult = ApiClient.weatherService.getCurrentWeather(
                        latitude = geo.lat.toDouble(),
                        longitude = geo.lon.toDouble()
                    )
                    _weather.value = weatherResult
                    _weatherError.value = null // Clear any previous errors
                } else {
                    _weather.value = null
                    _weatherError.value = "City '${_selectedCity.value}' not found"
                }
            } catch (e: retrofit2.HttpException) {
                _weather.value = null
                _weatherError.value = "Weather service error: ${e.code()}"
            } catch (e: java.net.UnknownHostException) {
                _weather.value = null
                _weatherError.value = "No internet connection"
            } catch (e: java.net.SocketTimeoutException) {
                _weather.value = null
                _weatherError.value = "Request timeout"
            } catch (e: com.google.gson.JsonSyntaxException) {
                _weather.value = null
                _weatherError.value = "Weather data format error"
            } catch (e: Exception) {
                _weather.value = null
                _weatherError.value = "Weather service unavailable: ${e.message}"
                e.printStackTrace() // Log for debugging
            } finally {
                _isWeatherLoading.value = false
            }
        }
    }



    fun retryWeather() {
        fetchWeather()
    }

    fun exportGoalsAsPdfWithChart(context: Context) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "PersonaPulse Goals Export"

        printManager.print(
            jobName,
            object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: LayoutResultCallback?,
                    metadata: Bundle?
                ) {
                    val builder = PrintDocumentInfo.Builder("goals_export.pdf")
                    builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    builder.setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    callback?.onLayoutFinished(builder.build(), true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    destination?.fileDescriptor?.let { fd ->
                        PrintWriter(FileOutputStream(fd)).use { out ->
                            out.println("PersonaPulse Goals\n")
                            out.println("Total: ${_goals.value.size}")
                            out.println("Completed: ${_goals.value.count { it.isCompleted }}")
                            out.println("Incomplete: ${_goals.value.count { !it.isCompleted }}\n")

                            _goals.value.forEach {
                                val date = it.dueDate?.let {
                                    " (Due: ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it))})"
                                } ?: ""
                                out.println("${if (it.isCompleted) "[✓]" else "[ ]"} ${it.title}$date")
                            }
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    }
                }
            },
            null
        )
    }

    private fun saveGoals(goals: List<TodoData>) {
        viewModelScope.launch {
            try {
                val json = Json.encodeToString(goals)
                file.writeText(json)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadGoals() {
        viewModelScope.launch {
            try {
                if (file.exists()) {
                    val json = file.readText()
                    val loaded = Json.decodeFromString<List<TodoData>>(json)
                    _goals.value = loaded
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}