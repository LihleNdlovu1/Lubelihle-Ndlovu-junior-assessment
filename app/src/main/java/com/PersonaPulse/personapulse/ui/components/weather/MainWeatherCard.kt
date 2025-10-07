package com.PersonaPulse.personapulse.ui.components.weather

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Single Responsibility: Display the main weather information card
 * Only changes when main weather card display logic changes
 */
@Composable
fun MainWeatherCard(
    weatherData: WeatherDisplayData,
    animateWeatherIcon: Boolean,
    animateBackground: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (animateWeatherIcon) 1.05f else 1f,
        animationSpec = tween(2000),
        label = "scale"
    )
    
    val backgroundScale by animateFloatAsState(
        targetValue = if (animateBackground) 1.02f else 1f,
        animationSpec = tween(4000),
        label = "backgroundScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(backgroundScale),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location
            Text(
                text = weatherData.location,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.scale(scale)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Weather Icon
            WeatherIconView(
                condition = weatherData.condition,
                animateWeatherIcon = animateWeatherIcon
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Temperature with gradient
            Text(
                text = weatherData.temperature,
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Condition
            Text(
                text = weatherData.condition,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}