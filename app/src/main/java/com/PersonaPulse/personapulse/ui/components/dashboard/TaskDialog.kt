package com.PersonaPulse.personapulse.ui.components.dashboard

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TaskDialog(
    showDialog: Boolean,
    isEditing: Boolean,
    taskTitle: String,
    onTitleChange: (String) -> Unit,
    taskDescription: String,
    onDescriptionChange: (String) -> Unit,
    priority: String,
    onPriorityChange: (String) -> Unit,
    dueDate: Long?,
    onDueDateChange: (Long?) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    if (showDialog) {
        val context = LocalContext.current
        val cal = remember { Calendar.getInstance() }
        var expanded by remember { mutableStateOf(false) }
        
        val priorities = listOf("High", "Medium", "Low")
        val priorityColors = mapOf(
            "High" to Color(0xFFFF4444),
            "Medium" to Color(0xFFFFB347),
            "Low" to Color(0xFF4CAF50)
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { 
                Text(
                    text = if (isEditing) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = onTitleChange,
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = onDescriptionChange,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Priority Dropdown
                    Box {
                        OutlinedTextField(
                            value = priority.ifEmpty { "Select Priority" },
                            onValueChange = { },
                            label = { Text("Priority") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown, 
                                    contentDescription = null,
                                    modifier = Modifier.clickable { expanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                        )
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            priorities.forEach { priorityOption ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(12.dp)
                                                    .height(12.dp)
                                                    .background(
                                                        priorityColors[priorityOption] ?: Color.Gray,
                                                        RoundedCornerShape(6.dp)
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(priorityOption)
                                        }
                                    },
                                    onClick = {
                                        onPriorityChange(priorityOption)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    cal.set(year, month, day, 0, 0)
                                    onDueDateChange(cal.timeInMillis)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick Due Date")
                    }

                    dueDate?.let { date ->
                        val formatted = try {
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(date))
                        } catch (e: Exception) {
                            "Date selected"
                        }
                        Text(
                            text = "Due: $formatted", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onSave,
                    enabled = taskTitle.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}




