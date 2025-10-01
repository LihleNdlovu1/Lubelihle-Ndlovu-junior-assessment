package com.PersonaPulse.personapulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.navigation.Screen
import com.PersonaPulse.personapulse.ui.components.common.BottomNavigationBar
import com.PersonaPulse.personapulse.ui.components.common.EmptyState
import com.PersonaPulse.personapulse.ui.components.common.PersonaPulseTitle
import com.PersonaPulse.personapulse.ui.components.dashboard.TaskDialog
import com.PersonaPulse.personapulse.ui.components.dashboard.TaskProgressCard
import com.PersonaPulse.personapulse.ui.components.dashboard.TodoListSection
import com.PersonaPulse.personapulse.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = viewModel()) {
    val todos by viewModel.todos.collectAsState()
    
    var showDialog by remember { mutableStateOf(false) }
    var newTodoTitle by remember { mutableStateOf("") }
    var newTodoDescription by remember { mutableStateOf("") }
    var newTodoPriority by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var selectedTodoIndex by remember { mutableStateOf(-1) }
    var dueDate by remember { mutableStateOf<Long?>(null) }

    val (incompleteTodos, completedTodos) = remember(todos) {
        todos.partition { todo -> !todo.isCompleted }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { PersonaPulseTitle() },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate(Screen.Notifications.route)
                    }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .padding(bottom = 80.dp), // Add space for bottom navigation
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Task Progress Card
                    TaskProgressCard(
                        inProgressCount = incompleteTodos.size,
                        completedCount = completedTodos.size
                    )
                }
                
                item {
                    // Todo List Section
                    if (incompleteTodos.isNotEmpty() || completedTodos.isNotEmpty()) {
                        TodoListSection(
                            incompleteGoals = incompleteTodos,
                            completedGoals = completedTodos,
                            onToggleGoal = { index -> viewModel.toggleTodoCompleted(index) },
                            onEditGoal = { index ->
                                val todo = todos[index]
                                selectedTodoIndex = index
                                newTodoTitle = todo.title
                                newTodoDescription = todo.description ?: ""
                                newTodoPriority = todo.priority.name
                                dueDate = todo.dueDate
                                isEditing = true
                                showDialog = true
                            },
                            onDeleteGoal = { index -> viewModel.deleteTodo(index) },
                            allGoals = todos
                        )
                    } else {
                        // Empty State
                        EmptyState()
                    }
                }
            }
            
            // Bottom Navigation Bar
            BottomNavigationBar(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        "history" -> navController.navigate(Screen.History.route)
                        "weather" -> navController.navigate(Screen.Weather.route)
                        "analytics" -> navController.navigate(Screen.Analytics.route)
                    }
                },
                onAddTask = {
                    showDialog = true
                    isEditing = false
                    newTodoTitle = ""
                    newTodoDescription = ""
                    newTodoPriority = ""
                    dueDate = null
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Task Dialog
    TaskDialog(
        showDialog = showDialog,
        isEditing = isEditing,
        taskTitle = newTodoTitle,
        onTitleChange = { newTodoTitle = it },
        taskDescription = newTodoDescription,
        onDescriptionChange = { newTodoDescription = it },
        priority = newTodoPriority,
        onPriorityChange = { newTodoPriority = it },
        dueDate = dueDate,
        onDueDateChange = { dueDate = it },
        onDismiss = { 
            showDialog = false
            newTodoTitle = ""
            newTodoDescription = ""
            newTodoPriority = ""
            dueDate = null
            isEditing = false
        },
        onSave = {
            if (newTodoTitle.isNotBlank()) {
                val priority = when (newTodoPriority) {
                    "High" -> Priority.HIGH
                    "Medium" -> Priority.MEDIUM
                    "Low" -> Priority.LOW
                    else -> Priority.LOW
                }
                
                if (isEditing) {
                    viewModel.updateTodo(
                        selectedTodoIndex, 
                        newTodoTitle.trim(), 
                        newTodoDescription.trim().takeIf { it.isNotBlank() },
                        priority,
                        dueDate
                    )
                } else {
                    viewModel.addTodo(
                        newTodoTitle.trim(), 
                        newTodoDescription.trim().takeIf { it.isNotBlank() },
                        priority,
                        dueDate
                    )
                }
                showDialog = false
                newTodoTitle = ""
                newTodoDescription = ""
                newTodoPriority = ""
                dueDate = null
                isEditing = false
            }
        }
    )
}