package com.PersonaPulse.personapulse.ui.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single Responsibility: Display sunrise and sunset times
 * Only changes when sun times display logic changes
 */
@Composable
fun SunTimesCard(
    sunrise: String,
    sunset: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 15.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sun Times",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SunTimeRow(
                title = "Sunrise",
                time = sunrise,
                icon = "☀️",
                color = Color(0xFFFFA500)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SunTimeRow(
                title = "Sunset",
                time = sunset,
                icon = "🌅",
                color = Color.Red
            )
        }
    }
}

/**
 * Single Responsibility: Display individual sun time row
 * Only changes when sun time row display logic changes
 */
@Composable
fun SunTimeRow(
    title: String,
    time: String,
    icon: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Text(
                text = time,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}




