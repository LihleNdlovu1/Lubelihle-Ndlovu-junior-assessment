package com.PersonaPulse.personapulse.ui.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Single Responsibility: Display animated background based on weather condition
 * Only changes when background animation logic changes
 */
@Composable
fun WeatherBackgroundView(
    condition: String,
    modifier: Modifier = Modifier
) {
    val animateGradient = remember { mutableStateOf(true) }
    
    val gradientColors = when {
        condition.contains("Sun", ignoreCase = true) || condition.contains("Clear", ignoreCase = true) -> 
            listOf(Color(0xFFFFA500), Color(0xFFFFD700), Color(0xFF87CEEB))
        condition.contains("Cloud", ignoreCase = true) -> 
            listOf(Color(0xFF808080), Color(0xFF4169E1), Color(0xFF9370DB))
        condition.contains("Rain", ignoreCase = true) -> 
            listOf(Color(0xFF0000FF), Color(0xFF808080), Color(0xFF000000))
        else -> 
            listOf(Color(0xFF4169E1), Color(0xFF9370DB), Color(0xFFFF69B4))
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors.map { it.copy(alpha = 0.6f) },
                    start = if (animateGradient.value) androidx.compose.ui.geometry.Offset(0.0f, 0.0f) else androidx.compose.ui.geometry.Offset(1.0f, 1.0f),
                    end = if (animateGradient.value) androidx.compose.ui.geometry.Offset(1.0f, 1.0f) else androidx.compose.ui.geometry.Offset(0.0f, 0.0f)
                )
            )
    )
}

/**
 * Single Responsibility: Display particle effects
 * Only changes when particle effect logic changes
 */
@Composable
fun ParticleEffectView(
    modifier: Modifier = Modifier
) {
    val particles = remember { generateParticles() }
    
    Box(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            Box(
                modifier = Modifier
                    .size(particle.size.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
            )
        }
    }
}

/**
 * Single Responsibility: Generate particle data
 * Only changes when particle generation algorithm changes
 */
private fun generateParticles(): List<Particle> {
    return (0..20).map {
        Particle(
            size = (Random.nextFloat() * 4 + 2).toFloat(),
            x = (Random.nextFloat() * 400).toFloat(),
            y = (Random.nextFloat() * 800).toFloat()
        )
    }
}

/**
 * Data class for particle information
 */
data class Particle(
    val size: Float,
    val x: Float,
    val y: Float
)
