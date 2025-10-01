package com.PersonaPulse.personapulse.ui.components.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PersonaPulse.personapulse.model.TodoData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoCard(
    goal: TodoData,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    // Animation for completion state
    val completionAlpha by animateFloatAsState(
        targetValue = if (goal.isCompleted) 0.6f else 1f,
        animationSpec = tween(300),
        label = "completion_alpha"
    )

    val checkboxScale by animateFloatAsState(
        targetValue = if (goal.isCompleted) 1.1f else 1f,
        animationSpec = tween(200),
        label = "checkbox_scale"
    )

    // Priority colors and gradients
    val priorityColors = when (goal.priority.name.lowercase()) {
        "high" -> listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E))
        "medium" -> listOf(Color(0xFFFFB347), Color(0xFFFFC777))
        "low" -> listOf(Color(0xFF4ECDC4), Color(0xFF7FDDCD))
        "overdue" -> listOf(Color(0xFFFF4444), Color(0xFFFF6666))
        else -> listOf(Color(0xFF6C5CE7), Color(0xFF8B7ED8))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(completionAlpha),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (goal.isCompleted) 2.dp else 8.dp
        ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header with priority indicator and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Priority indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(priorityColors)
                        )
                        .align(Alignment.Top)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = if (goal.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )

                // Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color(0xFF6C5CE7)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Checkbox(
                        checked = goal.isCompleted,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50),
                            uncheckedColor = Color(0xFFBDBDBD),
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.scale(checkboxScale)
                    )
                }
            }

            // Description
            goal.description?.takeIf { it.isNotBlank() }?.let { description ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority chip
                InfoChip(
                    icon = Icons.Outlined.Flag,
                    text = goal.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    backgroundColor = priorityColors[0].copy(alpha = 0.1f),
                    textColor = priorityColors[0]
                )

                // Category chip
                goal.category?.takeIf { it.isNotBlank() }?.let { category ->
                    InfoChip(
                        icon = Icons.Outlined.Category,
                        text = category,
                        backgroundColor = Color(0xFF6C5CE7).copy(alpha = 0.1f),
                        textColor = Color(0xFF6C5CE7)
                    )
                }
            }

            // Date information
            val hasDateInfo = goal.dueDate != null || goal.reminderTime != null
            if (hasDateInfo) {
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Due date
                    goal.dueDate?.let { dueDate ->
                        DateInfoRow(
                            icon = Icons.Outlined.CalendarToday,
                            label = "Due",
                            date = dueDate,
                            color = if (isOverdue(dueDate)) Color(0xFFFF6B6B) else Color(0xFF6C5CE7)
                        )
                    }

                    // Reminder
                    goal.reminderTime?.let { reminderTime ->
                        DateInfoRow(
                            icon = Icons.Outlined.Notifications,
                            label = "Reminder",
                            date = reminderTime,
                            color = Color(0xFF4CAF50),
                            includeTime = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = textColor
        )
    }
}

@Composable
private fun DateInfoRow(
    icon: ImageVector,
    label: String,
    date: Long,
    color: Color,
    includeTime: Boolean = false
) {
    val dateFormat = if (includeTime) "MMM dd, HH:mm" else "MMM dd, yyyy"
    val formatted = SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date(date))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label: $formatted",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
        )
    }
}

private fun isOverdue(dueDate: Long): Boolean {
    return Date(dueDate).before(Date())
}