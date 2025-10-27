package com.PersonaPulse.personapulse.ui.components.weather

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
 * Single Responsibility: Display outfit suggestions based on weather
 * Only changes when outfit suggestion logic changes
 */
@Composable
fun OutfitSuggestionCard(
    weatherData: WeatherDisplayData,
    modifier: Modifier = Modifier
) {
    val suggestion = getOutfitSuggestion(weatherData)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👕",
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "What to Wear Today",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = suggestion,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * Single Responsibility: Generate outfit suggestions based on weather data
 * Only changes when outfit suggestion algorithm changes
 */
private fun getOutfitSuggestion(weatherData: WeatherDisplayData): String {
    val temp = weatherData.temperature.replace("°", "").toIntOrNull() ?: 20
    val condition = weatherData.condition.lowercase()
    
    return when {
        condition.contains("rain") -> "🌧️ It's rainy today! Wear a waterproof jacket and bring an umbrella. Don't forget waterproof shoes."
        condition.contains("snow") -> "❄️ Snow expected! Bundle up with a heavy coat, warm gloves, and insulated boots. Layer up!"
        temp <= 10 -> "🧥 Quite cold today! Wear a warm coat, scarf, and boots. Consider thermal layers underneath."
        temp <= 20 -> "😎 Cool weather ahead! A light jacket or sweater will keep you comfortable."
        temp <= 28 -> "🌤️ Perfect weather! A t-shirt or light blouse will be just right for today."
        else -> "☀️ Hot day ahead! Light, breathable clothing and sun protection recommended. Stay hydrated!"
    }
}







