package com.PersonaPulse.personapulse.ui.components.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
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
    dueTime: Long?,
    onDueTimeChange: (Long?) -> Unit,
    category: String?,
    onCategoryChange: (String?) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    if (showDialog) {
        val context = LocalContext.current
        val cal = remember { Calendar.getInstance() }
        var priorityExpanded by remember { mutableStateOf(false) }
        var categoryExpanded by remember { mutableStateOf(false) }
        var titleError by remember { mutableStateOf<String?>(null) }
        var dateError by remember { mutableStateOf<String?>(null) }
        
        val priorities = listOf("High", "Medium", "Low")
        val categories = listOf("Work", "Personal", "Shopping", "Health", "Finance", "Education", "Other")
        val priorityColors = mapOf(
            "High" to Color(0xFFFF4444),
            "Medium" to Color(0xFFFFB347),
            "Low" to Color(0xFF4CAF50)
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF2C2C2C),
            title = { 
                Text(
                    text = if (isEditing) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { 
                            onTitleChange(it)
                            titleError = when {
                                it.isBlank() -> "Title cannot be empty"
                                it.length > 100 -> "Title too long (max 100 characters)"
                                else -> null
                            }
                        },
                        label = { Text("Task Title *", color = Color.White) },
                        modifier = Modifier.fillMaxWidth()
                            .testTag("TitleTextField"),
                        singleLine = true,
                        isError = titleError != null,
                        supportingText = titleError?.let { { Text(it, color = Color.Red) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFCDDC39),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFCDDC39)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { 
                            if (it.length <= 500) {
                                onDescriptionChange(it)
                            }
                        },
                        label = { Text("Description (optional)", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        supportingText = { 
                            Text(
                                "${taskDescription.length}/500",
                                color = if (taskDescription.length > 450) Color(0xFFFFB347) else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFCDDC39),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFCDDC39)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Priority Dropdown
                    Box {
                        OutlinedTextField(
                            value = priority.ifEmpty { "Select Priority" },
                            onValueChange = { },
                            label = { Text("Priority", color = Color.White) },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown, 
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.clickable { priorityExpanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { priorityExpanded = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFCDDC39),
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White
                            )
                        )
                        
                        DropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
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
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Category Dropdown
                    Box {
                        OutlinedTextField(
                            value = category ?: "Select Category (optional)",
                            onValueChange = { },
                            label = { Text("Category", color = Color.White) },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown, 
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.clickable { categoryExpanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryExpanded = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFCDDC39),
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White
                            )
                        )
                        
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            // Option to clear category
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    onCategoryChange(null)
                                    categoryExpanded = false
                                }
                            )
                            categories.forEach { categoryOption ->
                                DropdownMenuItem(
                                    text = { Text(categoryOption) },
                                    onClick = {
                                        onCategoryChange(categoryOption)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Date Picker Button
                    Button(
                        onClick = {
                            val today = Calendar.getInstance()
                            today.set(Calendar.HOUR_OF_DAY, 0)
                            today.set(Calendar.MINUTE, 0)
                            today.set(Calendar.SECOND, 0)
                            today.set(Calendar.MILLISECOND, 0)
                            
                            val datePicker = DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    cal.set(year, month, day, 23, 59, 59)
                                    val selectedDate = cal.timeInMillis
                                    
                                    // Validate date is not in the past
                                    if (selectedDate < today.timeInMillis) {
                                        dateError = "Due date cannot be in the past"
                                    } else {
                                        dateError = null
                                        onDueDateChange(selectedDate)
                                    }
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            )
                            // Set minimum date to today
                            datePicker.datePicker.minDate = today.timeInMillis
                            datePicker.show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCDDC39),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Pick Due Date (optional)")
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
                            color = Color(0xFFCDDC39),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Time Picker Button
                    Button(
                        onClick = {
                            val timeCal = Calendar.getInstance()
                            dueTime?.let { timeCal.timeInMillis = it }
                            
                            val timePicker = TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val timeCalendar = Calendar.getInstance()
                                    timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    timeCalendar.set(Calendar.MINUTE, minute)
                                    timeCalendar.set(Calendar.SECOND, 0)
                                    timeCalendar.set(Calendar.MILLISECOND, 0)
                                    onDueTimeChange(timeCalendar.timeInMillis)
                                },
                                timeCal.get(Calendar.HOUR_OF_DAY),
                                timeCal.get(Calendar.MINUTE),
                                false // 12-hour format
                            )
                            timePicker.show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCDDC39),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Pick Due Time (optional)")
                    }
                    
                    dueTime?.let { time ->
                        val formatted = try {
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time))
                        } catch (e: Exception) {
                            "Time selected"
                        }
                        Text(
                            text = "Time: $formatted", 
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCDDC39),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    // Show date error if any
                    dateError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Final validation before saving
                        when {
                            taskTitle.isBlank() -> titleError = "Title cannot be empty"
                            taskTitle.length > 100 -> titleError = "Title too long"
                            dateError != null -> { /* Keep existing date error */ }
                            else -> onSave()
                        }
                    },
                    enabled = taskTitle.isNotBlank() && 
                             taskTitle.length <= 100 && 
                             dateError == null
                ) {
                    Text("Save", color = Color(0xFFCDDC39))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}




