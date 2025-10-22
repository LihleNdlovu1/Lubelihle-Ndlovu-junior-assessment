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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.navigation.Screen
import com.PersonaPulse.personapulse.ui.components.common.BottomNavigationBar
import com.PersonaPulse.personapulse.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NotificationTaskCard(
    task: TodoData,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val priorityColors = when (task.priority) {
        Priority.HIGH -> Color(0xFFFF4444)
        Priority.MEDIUM -> Color(0xFFFFB347)
        Priority.LOW -> Color(0xFF4CAF50)
        Priority.OVERDUE -> Color(0xFFFF6B6B)
    }
    
    val isOverdue = task.dueDate?.let { dueDate ->
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val taskDate = Calendar.getInstance().apply {
            timeInMillis = dueDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        taskDate < today
    } ?: false
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) Color(0xFF2C1A1A) else Color(0xFF2C2C2C)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(priorityColors, CircleShape)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                task.description?.let { description ->
                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 2
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = priorityColors,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    task.dueDate?.let { dueDate ->
                        val formatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDate))
                        Text(
                            text = "Due: $formatted",
                            fontSize = 12.sp,
                            color = if (isOverdue) Color(0xFFFF6B6B) else Color.Gray
                        )
                    }
                }
            }
            
            // Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Checkbox
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFCDDC39),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.Black
                    ),
                    modifier = Modifier.size(24.dp)
                )
                
                // Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Complete",
                                        tint = Color(0xFFCDDC39)
                                    )
                                    Text(if (task.isCompleted) "Mark Incomplete" else "Mark Complete")
                                }
                            },
                            onClick = {
                                showMenu = false
                                onToggle()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFFF4444)
                                    )
                                    Text("Delete")
                                }
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyNotificationState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(bottom = 80.dp), // Add space for bottom navigation
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Relax you don't have a task due today",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Good job you're all caught up! 🎉",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController, viewModel: NotificationViewModel = hiltViewModel()) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(0) }
    
    // Show error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }
    
    // Filter tasks due today
    val tasksDueToday = todos.filter { task ->
        task.dueDate?.let { dueDate ->
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val taskDate = Calendar.getInstance().apply {
                timeInMillis = dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            taskDate == today && !task.isCompleted
        } ?: false
    }
    
    // Filter overdue tasks
    val overdueTasks = todos.filter { task ->
        task.dueDate?.let { dueDate ->
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val taskDate = Calendar.getInstance().apply {
                timeInMillis = dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            taskDate < today && !task.isCompleted
        } ?: false
    }
    
    // Filter upcoming tasks (next 7 days)
    val upcomingTasks = todos.filter { task ->
        task.dueDate?.let { dueDate ->
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val nextWeek = today + (7 * 86400000)
            
            val taskDate = Calendar.getInstance().apply {
                timeInMillis = dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            taskDate > today && taskDate <= nextWeek && !task.isCompleted
        } ?: false
    }
    
    // Sort by priority (High first, then Medium, then Low)
    val sortedTasksDueToday = tasksDueToday.sortedBy { task ->
        when (task.priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
            Priority.OVERDUE -> -1
        }
    }
    
    val sortedOverdueTasks = overdueTasks.sortedBy { task ->
        when (task.priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
            Priority.OVERDUE -> -1
        }
    }
    
    val sortedUpcomingTasks = upcomingTasks.sortedBy { task ->
        when (task.priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
            Priority.OVERDUE -> -1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Due Today",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFFF4444),
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        val currentTasks = when (selectedTab) {
            0 -> sortedTasksDueToday
            1 -> sortedOverdueTasks
            2 -> sortedUpcomingTasks
            else -> sortedTasksDueToday
        }
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Black,
                contentColor = Color(0xFFCDDC39),
                modifier = Modifier.padding(top = padding.calculateTopPadding())
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Today (${tasksDueToday.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Overdue (${overdueTasks.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Upcoming (${upcomingTasks.size})") }
                )
            }
            
            Box(modifier = Modifier.weight(1f)) {
                if (currentTasks.isEmpty()) {
                    EmptyNotificationState()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 80.dp), // Add space for bottom navigation
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Header with count
                            val tabName = when (selectedTab) {
                                0 -> "due today"
                                1 -> "overdue"
                                2 -> "upcoming"
                                else -> ""
                            }
                            Text(
                                text = "${currentTasks.size} task${if (currentTasks.size == 1) "" else "s"} $tabName",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(currentTasks) { task ->
                            NotificationTaskCard(
                                task = task,
                                onToggle = { viewModel.toggleTodoCompleted(task) },
                                onDelete = { viewModel.deleteTodo(task) }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        
        // Bottom Navigation Bar
        Box(modifier = Modifier.fillMaxSize()) {
            BottomNavigationBar(
                currentRoute = "notifications",
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
                    // Navigate back to dashboard to add task
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
