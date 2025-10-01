package com.PersonaPulse.personapulse.ui.components.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PersonaPulse.personapulse.model.Priority
import com.PersonaPulse.personapulse.model.TodoData
import kotlin.math.roundToInt

@Composable
fun TodoListSection(
    incompleteGoals: List<TodoData>,
    completedGoals: List<TodoData>,
    onToggleGoal: (Int) -> Unit,
    onEditGoal: (Int) -> Unit,
    onDeleteGoal: (Int) -> Unit,
    allGoals: List<TodoData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Incomplete tasks section
        if (incompleteGoals.isNotEmpty()) {
            Text(
                "In Progress",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            incompleteGoals.forEach { goal ->
                SwipeableTodoCard(
                    goal = goal,
                    onToggle = { onToggleGoal(allGoals.indexOf(goal)) },
                    onEdit = { onEditGoal(allGoals.indexOf(goal)) },
                    onDelete = { onDeleteGoal(allGoals.indexOf(goal)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Completed tasks section
        if (completedGoals.isNotEmpty()) {
            Text(
                "Complete",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            completedGoals.forEach { goal ->
                SwipeableTodoCard(
                    goal = goal,
                    onToggle = { onToggleGoal(allGoals.indexOf(goal)) },
                    onEdit = { onEditGoal(allGoals.indexOf(goal)) },
                    onDelete = { onDeleteGoal(allGoals.indexOf(goal)) }
                )
            }
        }
    }
}

@Composable
fun SwipeableTodoCard(
    goal: TodoData,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isSwipeToDelete by remember { mutableStateOf<Boolean>(false) }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isSwipeToDelete) -200f else offsetX,
        animationSpec = tween(300),
        label = "offsetX"
    )
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Delete background with better styling
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFFF4444),
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Delete",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Task card with improved gesture handling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { /* Optional: Add haptic feedback here */ },
                        onDragEnd = {
                            if (offsetX < -120) {
                                // Swipe far enough to delete
                                isSwipeToDelete = true
                                onDelete()
                            } else {
                                // Snap back to original position
                                offsetX = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        val newOffset = offsetX + dragAmount.x
                        offsetX = newOffset.coerceIn(-200f, 0f)
                    }
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            TodoCard(
                goal = goal,
                onToggle = onToggle,
                onEdit = onEdit
            )
        }
    }
}

@Composable
fun TodoCard(
    goal: TodoData,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColors = when (goal.priority) {
        Priority.HIGH -> Color(0xFFFF4444)
        Priority.MEDIUM -> Color(0xFFFFB347)
        Priority.LOW -> Color(0xFF4CAF50)
        Priority.OVERDUE -> Color(0xFF9C27B0)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox with better styling
            androidx.compose.material3.Checkbox(
                checked = goal.isCompleted,
                onCheckedChange = { onToggle() },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.title,
                    color = if (goal.isCompleted) Color.Gray else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                
                if (!goal.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = goal.description,
                        color = if (goal.isCompleted) Color.Gray else Color.Gray,
                        fontSize = 14.sp,
                        textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority badge with improved styling
                Box(
                    modifier = Modifier
                        .background(
                            priorityColors.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = goal.priority.name,
                        color = priorityColors,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
