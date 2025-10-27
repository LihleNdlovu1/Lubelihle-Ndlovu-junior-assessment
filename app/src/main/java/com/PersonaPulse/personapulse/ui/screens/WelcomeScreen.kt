package com.PersonaPulse.personapulse.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.PersonaPulse.personapulse.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    navController: NavController,
    onGetStarted: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        WelcomePage(
            icon = Icons.Default.Star,
            title = "Welcome to PersonaPulse",
            description = "Your personal productivity companion that helps you stay organized and achieve your goals.",
            color = Color(0xFFCDDC39)
        ),
        WelcomePage(
            icon = Icons.Default.CheckCircle,
            title = "Smart Task Management",
            description = "Create, organize, and track your tasks with intelligent reminders and priority management.",
            color = Color(0xFFD4E157)
        ),
        WelcomePage(
            icon = Icons.Default.TrendingUp,
            title = "Track Your Progress",
            description = "Monitor your productivity with detailed analytics and insights to improve your workflow.",
            color = Color(0xFFE6EE9C)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // App Logo/Icon
            AppLogo()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Page Content
            WelcomePageContent(
                page = pages[currentPage],
                isVisible = true
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Page Indicators
            PageIndicators(
                currentPage = currentPage,
                totalPages = pages.size
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Navigation Buttons
            WelcomeNavigation(
                currentPage = currentPage,
                totalPages = pages.size,
                onNext = { 
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onGetStarted()
                    }
                },
                onSkip = { onGetStarted() },
                onGetStarted = { onGetStarted() }
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun AppLogo() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "logo_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "logo_alpha"
    )
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .alpha(alpha)
            .background(
                Color(0xFFCDDC39),
                RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_persona_pulse_logo),
            contentDescription = "PersonaPulse Logo",
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun WelcomePageContent(
    page: WelcomePage,
    isVisible: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "content_alpha"
    )
    
    val translateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(500),
        label = "content_translate"
    )
    
    Column(
        modifier = Modifier
            .alpha(alpha)
            .offset(y = translateY.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    page.color.copy(alpha = 0.2f),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = page.color,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = page.title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        Text(
            text = page.description,
            color = Color(0xFFCCCCCC),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun PageIndicators(
    currentPage: Int,
    totalPages: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isActive = index == currentPage
            val width by animateFloatAsState(
                targetValue = if (isActive) 24f else 8f,
                animationSpec = tween(300),
                label = "indicator_width"
            )
            val color = if (isActive) Color(0xFFCDDC39) else Color(0xFF444444)
            
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun WelcomeNavigation(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onGetStarted: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Primary Button
        Button(
            onClick = if (currentPage < totalPages - 1) onNext else onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCDDC39),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (currentPage < totalPages - 1) "Next" else "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Secondary Button
        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Skip",
                color = Color(0xFF888888),
                fontSize = 16.sp
            )
        }
    }
}

data class WelcomePage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)
