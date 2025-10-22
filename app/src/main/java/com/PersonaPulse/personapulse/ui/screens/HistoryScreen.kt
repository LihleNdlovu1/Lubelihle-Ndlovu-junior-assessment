package com.PersonaPulse.personapulse.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.ui.components.common.BottomNavigationBar
import com.PersonaPulse.personapulse.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val todos by viewModel.todos.collectAsState()
    
    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) }
    
    // Filter completed tasks with search and priority filter
    val completedTasks = todos.filter { todo ->
        val isCompleted = todo.isCompleted
        
        val matchesSearch = searchQuery.isEmpty() || 
            todo.title.contains(searchQuery, ignoreCase = true) ||
            (todo.description?.contains(searchQuery, ignoreCase = true) == true)
        
        val matchesPriority = selectedPriorityFilter == null || todo.priority == selectedPriorityFilter
        
        isCompleted && matchesSearch && matchesPriority
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "History",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .padding(bottom = 80.dp), // Add space for bottom navigation
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                placeholder = { Text("Search history...", color = Color.Gray) },
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
                                    modifier = Modifier.padding(top = 8.dp)
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
                    // Header with count
                    Text(
                        text = "Completed Tasks (${completedTasks.size})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                if (completedTasks.isEmpty()) {
                    item {
                        EmptyHistoryState()
                    }
                } else {
                    
                    items(completedTasks) { task ->
                        CompletedTaskCard(
                            task = task,
                            onToggle = { viewModel.toggleTodo(task) }
                        )
                    }
                }
            }
            
            // Bottom Navigation Bar
            BottomNavigationBar(
                currentRoute = "history",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                        "history" -> navController.navigate("history")
                        "weather" -> navController.navigate("weather")
                        "analytics" -> navController.navigate("analytics")
                    }
                },
                onAddTask = {
                    // Navigate back to dashboard to add task
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CompletedTaskCard(
    task: TodoData,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColors = when (task.priority) {
        Priority.HIGH -> Color(0xFFFF4444)
        Priority.MEDIUM -> Color(0xFFFFB347)
        Priority.LOW -> Color(0xFF4CAF50)
        Priority.OVERDUE -> Color(0xFF9C27B0)
    }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox to untick and move back to ongoing
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color(0xFFBDBDBD),
                    checkmarkColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Task title with strikethrough
                Text(
                    text = task.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.LineThrough
                )
                
                // Task description if available
                if (!task.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority and completion info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority badge
                    Box(
                        modifier = Modifier
                            .background(
                                priorityColors.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = task.priority.name,
                            color = priorityColors,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Completion time
                    if (task.completedAt != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Completed at",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completed ${dateFormat.format(Date(task.completedAt))} at ${timeFormat.format(Date(task.completedAt))}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(bottom = 80.dp), // Add space for bottom navigation
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Completed Tasks",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Complete some tasks to see them here",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
