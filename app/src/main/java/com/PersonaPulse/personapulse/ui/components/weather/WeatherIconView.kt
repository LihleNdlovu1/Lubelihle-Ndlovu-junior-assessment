package com.PersonaPulse.personapulse.ui.components.weather

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Single Responsibility: Display and animate weather icons based on condition
 * Only changes when weather icon display logic changes
 */
@Composable
fun WeatherIconView(
    condition: String,
    animateWeatherIcon: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (animateWeatherIcon) 1.1f else 1f,
        animationSpec = tween(3000),
        label = "iconScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (animateWeatherIcon) 5f else -5f,
        animationSpec = tween(3000),
        label = "iconRotation"
    )
    
    val icon = when {
        condition.contains("Sun", ignoreCase = true) || condition.contains("Clear", ignoreCase = true) -> Icons.Default.WbSunny
        condition.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
        condition.contains("Rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }
    
    val iconColor = when {
        condition.contains("Sun", ignoreCase = true) || condition.contains("Clear", ignoreCase = true) -> Color.Yellow
        condition.contains("Cloud", ignoreCase = true) -> Color.Gray
        condition.contains("Rain", ignoreCase = true) -> Color.Blue
        else -> Color.Yellow
    }
    
    Icon(
        imageVector = icon,
        contentDescription = condition,
        tint = iconColor,
        modifier = modifier
            .size(60.dp)
            .scale(scale)
            .rotate(rotation)
    )
}
