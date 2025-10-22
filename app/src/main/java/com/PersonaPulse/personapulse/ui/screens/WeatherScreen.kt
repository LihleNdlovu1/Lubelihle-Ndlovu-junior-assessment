package com.PersonaPulse.personapulse.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.WbSunny
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.PersonaPulse.personapulse.ui.components.common.BottomNavigationBar
import com.PersonaPulse.personapulse.viewmodel.WeatherViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, viewModel: WeatherViewModel = hiltViewModel()) {
    val weatherResponse by viewModel.weather.collectAsState()
    val weatherError by viewModel.weatherError.collectAsState()
    val isWeatherLoading by viewModel.isLoading.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isUsingCurrentLocation by viewModel.isUsingCurrentLocation.collectAsState()
    
    var showDetails by remember { mutableStateOf(false) }
    var animateWeatherIcon by remember { mutableStateOf(false) }
    var animateBackground by remember { mutableStateOf(false) }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.fetchWeatherForCurrentLocation()
        }
    }
    
    // Convert API response to UI data
    val weatherData = remember(weatherResponse, selectedCity) {
        weatherResponse?.current_weather?.let { currentWeather ->
            val temp = currentWeather.temperature.toInt()
            val condition = when (currentWeather.weathercode) {
                0 -> "Clear"
                1, 2 -> "Partly Cloudy"
                3 -> "Cloudy"
                45, 48 -> "Foggy"
                51, 53, 55 -> "Drizzle"
                61, 63, 65 -> "Rainy"
                71, 73, 75 -> "Snowy"
                80, 81, 82 -> "Rain Showers"
                95, 96, 99 -> "Thunderstorm"
                else -> "Unknown"
            }
            
            WeatherData(
                location = selectedCity ?: "Unknown",
                temperature = "${temp}°",
                condition = condition,
                feelsLike = "${temp - 2}°", // Approximate feels like
                humidity = "${(currentWeather.windspeed * 3).toInt()}%", // Mock humidity
                sunrise = "06:30", // Mock sunrise
                sunset = "18:45"   // Mock sunset
            )
        } ?: WeatherData(
            location = selectedCity ?: "Johannesburg",
            temperature = "24°",
            condition = "Sunny",
            feelsLike = "22°",
            humidity = "65%",
            sunrise = "06:30",
            sunset = "18:45"
        )
    }
    
    LaunchedEffect(Unit) {
        viewModel.fetchWeather()
        animateWeatherIcon = true
        animateBackground = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Weather",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (viewModel.hasLocationPermission()) {
                                viewModel.fetchWeatherForCurrentLocation()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Use Current Location",
                            tint = if (isUsingCurrentLocation) Color(0xFF4CAF50) else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Animated background
            AnimatedBackgroundView(condition = weatherData.condition)
            
            // Floating particles
            ParticleEffectView()
            
            if (isWeatherLoading) {
                ModernLoadingView()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 80.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Show error message if present
                    weatherError?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Red.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Main Weather Card
                    MainWeatherCard(
                        weatherData = weatherData,
                        animateWeatherIcon = animateWeatherIcon,
                        animateBackground = animateBackground
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sun Times and Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SunTimesCard(
                            sunrise = weatherData.sunrise,
                            sunset = weatherData.sunset,
                            modifier = Modifier.weight(1f)
                        )
                        
                        WeatherMetricsCard(
                            feelsLike = weatherData.feelsLike,
                            humidity = weatherData.humidity,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Outfit Suggestion Card
                    OutfitSuggestionCard(weatherData = weatherData)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Toggle Details Button
                    Card(
                        onClick = { showDetails = !showDetails },
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showDetails) "Hide Details" else "Show Details",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.rotate(if (showDetails) 90f else -90f)
                            )
                        }
                    }
                    
                    if (showDetails) {
                        Spacer(modifier = Modifier.height(16.dp))
                        WeatherChartCard(weatherResponse = weatherResponse)
                    }
                }
            }
            
            // Bottom Navigation Bar
            BottomNavigationBar(
                currentRoute = "weather",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                        "history" -> navController.navigate("history")
                        "weather" -> navController.navigate("weather")
                        "analytics" -> navController.navigate("analytics")
                    }
                },
                onAddTask = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun MainWeatherCard(
    weatherData: WeatherData,
    animateWeatherIcon: Boolean,
    animateBackground: Boolean
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
        modifier = Modifier
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

@Composable
fun WeatherIconView(
    condition: String,
    animateWeatherIcon: Boolean
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
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .rotate(rotation)
    )
}

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

@Composable
fun WeatherMetricsCard(
    feelsLike: String,
    humidity: String,
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
                text = "Feels Like",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feelsLike,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Humidity $humidity",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun OutfitSuggestionCard(weatherData: WeatherData) {
    val suggestion = getOutfitSuggestion(weatherData)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
fun WeatherChartCard(weatherResponse: com.PersonaPulse.personapulse.model.WeatherResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "24-Hour Forecast",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            weatherResponse?.hourly?.let { hourly ->
                // Display hourly forecast data
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hourly.time.size.coerceAtMost(24)) { index ->
                        HourlyForecastItem(
                            time = hourly.time[index],
                            temperature = hourly.temperature_2m[index],
                            weatherCode = hourly.weathercode[index]
                        )
                    }
                }
            } ?: run {
                // Fallback if no hourly data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No forecast data available",
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(time: String, temperature: Double, weatherCode: Int) {
    val hour = time.substring(11, 16) // Extract HH:MM from ISO 8601
    val weatherIcon = when (weatherCode) {
        0 -> "☀️"
        1, 2 -> "⛅"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55 -> "🌦️"
        61, 63, 65 -> "🌧️"
        71, 73, 75 -> "❄️"
        80, 81, 82 -> "🌧️"
        95, 96, 99 -> "⛈️"
        else -> "🌤️"
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .width(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hour,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = weatherIcon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${temperature.toInt()}°",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}@Composable
fun ModernLoadingView() {
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation = (rotation + 10f) % 360f
            delay(50)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Outer circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                    )
                    
                    // Inner rotating circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .rotate(rotation)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Blue,
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Loading Weather Data...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Getting your location and current conditions",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AnimatedBackgroundView(condition: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

@Composable
fun ParticleEffectView() {
    val particles = remember { generateParticles() }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
private fun generateParticles(): List<Particle> {
    return (0..20).map {
        Particle(
            size = (Random.nextFloat() * 4 + 2).toFloat(),
            x = (Random.nextFloat() * 400).toFloat(),
            y = (Random.nextFloat() * 800).toFloat()
        )
    }
}

data class Particle(
    val size: Float,
    val x: Float,
    val y: Float
)

data class WeatherData(
    val location: String,
    val temperature: String,
    val condition: String,
    val feelsLike: String,
    val humidity: String,
    val sunrise: String,
    val sunset: String
)

private fun getOutfitSuggestion(weatherData: WeatherData): String {
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



