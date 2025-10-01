package com.PersonaPulse.personapulse.ui.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskProgressCard(
    inProgressCount: Int,
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    val totalTasks = inProgressCount + completedCount
    val progressPercentage = if (totalTasks > 0) completedCount.toFloat() / totalTasks.toFloat() else 0f
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Text(
                text = "Task Progress",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF404040)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // In Progress
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "In Progress",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$inProgressCount tasks",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFB347)
                    )
                }
                
                // Completed
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$completedCount tasks",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Percentage
            Text(
                text = "${(progressPercentage * 100).toInt()}% Complete",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
