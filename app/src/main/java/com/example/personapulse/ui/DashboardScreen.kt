package com.example.personapulse.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.personapulse.ui.components.GoalCard
import com.example.personapulse.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = viewModel()) {
    val goals by viewModel.goals.collectAsState()
    val weatherResponse by viewModel.weather.collectAsState()
    val weatherError by viewModel.weatherError.collectAsState()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newGoalTitle by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var selectedGoalIndex by remember { mutableStateOf(-1) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.fetchWeather()
    }

    val (incompleteGoals, completedGoals) = remember(goals) {
        goals.partition { !it.isCompleted }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todo") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
                isEditing = false
                newGoalTitle = ""
                dueDate = null
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(16.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        if (isWeatherLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Text("Loading weather...", style = MaterialTheme.typography.bodySmall)
                        } else if (weatherError != null) {
                            Text(
                                text = "Weather unavailable",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            TextButton(
                                onClick = { viewModel.retryWeather() },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text("Retry", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {

                            weatherResponse?.let { weather ->
                                val temperatureText = try {
                                    "${weather.current_weather.temperature.toInt()}°C"
                                } catch (e: Exception) {
                                    "Temperature unavailable"
                                }

                                Text(
                                    text = temperatureText,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = selectedCity,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            } ?: Text(
                                text = "No weather data",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }


                    weatherResponse?.let { weather ->
                        val isDayTime = try {
                            weather.current_weather.is_day == 1
                        } catch (e: Exception) {
                            true
                        }

                        Icon(
                            imageVector = if (isDayTime) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = if (isDayTime) "Day" else "Night",
                            modifier = Modifier.size(48.dp),
                            tint = if (isDayTime)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //to do section
            if (incompleteGoals.isNotEmpty()) {
                Text(
                    "Todo",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                incompleteGoals.forEach { goal ->
                    GoalCard(
                        goal,
                        onToggle = { viewModel.toggleGoalCompleted(goals.indexOf(goal)) },
                        onEdit = {
                            selectedGoalIndex = goals.indexOf(goal)
                            newGoalTitle = goal.title
                            dueDate = goal.dueDate
                            isEditing = true
                            showDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // completed task
            if (completedGoals.isNotEmpty()) {
                Text(
                    "Complete",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                completedGoals.forEach { goal ->
                    GoalCard(
                        goal,
                        onToggle = { viewModel.toggleGoalCompleted(goals.indexOf(goal)) },
                        onEdit = {
                            selectedGoalIndex = goals.indexOf(goal)
                            newGoalTitle = goal.title
                            dueDate = goal.dueDate
                            isEditing = true
                            showDialog = true
                        }
                    )
                }
            }


            if (incompleteGoals.isEmpty() && completedGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks yet. Tap + to add your first task!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    // editing and adding
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isEditing) "Edit Task" else "New Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val context = LocalContext.current
                    val cal = remember { Calendar.getInstance() }

                    Button(onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                cal.set(year, month, day, 0, 0)
                                dueDate = cal.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text("Pick Due Date")
                    }

                    dueDate?.let {
                        val formatted = try {
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                        } catch (e: Exception) {
                            "Date selected"
                        }
                        Text("Due: $formatted", style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newGoalTitle.isNotBlank()) {
                        if (isEditing) {
                            viewModel.updateGoal(selectedGoalIndex, newGoalTitle.trim(), dueDate)
                        } else {
                            viewModel.addGoal(newGoalTitle.trim(), dueDate)
                        }
                        showDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}