package com.example.personapulse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.personapulse.model.TodoData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalCard(
    goal: TodoData,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (goal.isCompleted) "✓ ${goal.title}" else goal.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                goal.dueDate?.let {
                    val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                    Text("Due: $formatted", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                Checkbox(
                    checked = goal.isCompleted,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}

