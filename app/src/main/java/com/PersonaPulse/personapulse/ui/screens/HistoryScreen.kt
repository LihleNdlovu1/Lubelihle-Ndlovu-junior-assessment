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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val viewModel: HistoryViewModel = viewModel()
    val todos by viewModel.todos.collectAsState()
    
    // Filter completed tasks
    val completedTasks = todos.filter { it.isCompleted }
    
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
            if (completedTasks.isEmpty()) {
                EmptyHistoryState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .padding(bottom = 80.dp), // Add space for bottom navigation
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                    
                    items(completedTasks) { task ->
                        CompletedTaskCard(task = task)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completed icon
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
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
