package com.PersonaPulse.personapulse.ui.components.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.PersonaPulse.personapulse.model.WeatherResponse

@Composable
fun WeatherSection(
    weatherResponse: WeatherResponse?,
    selectedCity: String,
    isWeatherLoading: Boolean,
    weatherError: String?,
    onRetryWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weather content
            Column {
                when {
                    isWeatherLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text(
                            "Loading weather...", 
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    weatherError != null -> {
                        Text(
                            text = "Weather unavailable",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(
                            onClick = onRetryWeather,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Retry", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    weatherResponse != null -> {
                        val temperatureText = try {
                            "${weatherResponse.current_weather.temperature.toInt()}°C"
                        } catch (e: Exception) {
                            "Temperature unavailable"
                        }

                        Text(
                            text = temperatureText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = selectedCity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    else -> {
                        Text(
                            text = "No weather data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Weather icon
            weatherResponse?.let { weather ->
                val isDayTime = try {
                    weather.current_weather.is_day == 1
                } catch (e: Exception) {
                    true
                }

                Icon(
                    imageVector = if (isDayTime) Icons.Default.WbSunny else Icons.Default.NightsStay,
                    contentDescription = if (isDayTime) "Day" else "Night",
                    modifier = Modifier.size(48.dp),
                    tint = if (isDayTime)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}




