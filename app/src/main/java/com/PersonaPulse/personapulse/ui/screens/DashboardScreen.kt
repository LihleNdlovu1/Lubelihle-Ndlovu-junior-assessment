package com.PersonaPulse.personapulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showDialog by remember { mutableStateOf(false) }
    var newTodoTitle by remember { mutableStateOf("") }
    var newTodoDescription by remember { mutableStateOf("") }
    var newTodoPriority by remember { mutableStateOf("") }
    var newTodoCategory by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedTodoIndex by remember { mutableStateOf(-1) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var dueTime by remember { mutableStateOf<Long?>(null) }
    
    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) }

    // Filter and search todos
    val filteredTodos = remember(todos, searchQuery, selectedPriorityFilter) {
        todos.filter { todo ->
            val matchesSearch = searchQuery.isEmpty() || 
                todo.title.contains(searchQuery, ignoreCase = true) ||
                (todo.description?.contains(searchQuery, ignoreCase = true) == true)
            
            val matchesPriority = selectedPriorityFilter == null || todo.priority == selectedPriorityFilter
            
            matchesSearch && matchesPriority
        }
    }
    
    val (incompleteTodos, completedTodos) = remember(filteredTodos) {
        filteredTodos.partition { todo -> !todo.isCompleted }
    }
    
    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }
    
    // Show success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
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
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (errorMessage != null) Color(0xFFFF4444) else Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            }
        }
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
                    // Search and Filter Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Search Field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Search tasks...", color = Color.Gray) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFFCDDC39)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFCDDC39),
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color(0xFFCDDC39)
                                )
                            )
                            
                            // Filter Button
                            Box {
                                IconButton(
                                    onClick = { showFilterMenu = true },
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.FilterList,
                                        contentDescription = "Filter",
                                        tint = if (selectedPriorityFilter != null) Color(0xFFCDDC39) else Color.White
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showFilterMenu,
                                    onDismissRequest = { showFilterMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Priorities") },
                                        onClick = {
                                            selectedPriorityFilter = null
                                            showFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("High Priority") },
                                        onClick = {
                                            selectedPriorityFilter = Priority.HIGH
                                            showFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Medium Priority") },
                                        onClick = {
                                            selectedPriorityFilter = Priority.MEDIUM
                                            showFilterMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Low Priority") },
                                        onClick = {
                                            selectedPriorityFilter = Priority.LOW
                                            showFilterMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Active filter indicator
                        if (selectedPriorityFilter != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Filtered by: ${selectedPriorityFilter?.name}",
                                    color = Color(0xFFCDDC39),
                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
                
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
                            onToggleGoal = { todo -> viewModel.toggleTodoCompleted(todo) },
                            onEditGoal = { todo ->
                                selectedTodoIndex = todos.indexOf(todo)
                                newTodoTitle = todo.title
                                newTodoDescription = todo.description ?: ""
                                newTodoPriority = todo.priority.name
                                newTodoCategory = todo.category
                                dueDate = todo.dueDate
                                dueTime = todo.reminderTime
                                isEditing = true
                                showDialog = true
                            },
                            onDeleteGoal = { todo -> viewModel.deleteTodo(todo) },
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
                    newTodoCategory = null
                    dueDate = null
                    dueTime = null
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
        dueTime = dueTime,
        onDueTimeChange = { dueTime = it },
        category = newTodoCategory,
        onCategoryChange = { newTodoCategory = it },
        onDismiss = { 
            showDialog = false
            newTodoTitle = ""
            newTodoDescription = ""
            newTodoPriority = ""
            newTodoCategory = null
            dueDate = null
            dueTime = null
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
                    val todoToUpdate = todos[selectedTodoIndex]
                    viewModel.updateTodo(
                        todoToUpdate, 
                        newTodoTitle.trim(), 
                        newTodoDescription.trim().takeIf { it.isNotBlank() },
                        priority,
                        dueDate,
                        newTodoCategory,
                        dueTime
                    )
                } else {
                    viewModel.addTodo(
                        newTodoTitle.trim(), 
                        newTodoDescription.trim().takeIf { it.isNotBlank() },
                        priority,
                        dueDate,
                        newTodoCategory,
                        dueTime
                    )
                }
                showDialog = false
                newTodoTitle = ""
                newTodoDescription = ""
                newTodoPriority = ""
                newTodoCategory = null
                dueDate = null
                dueTime = null
                isEditing = false
            }
        }
    )
}