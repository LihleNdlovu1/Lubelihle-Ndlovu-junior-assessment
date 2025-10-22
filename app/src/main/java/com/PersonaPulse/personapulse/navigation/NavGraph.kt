package com.PersonaPulse.personapulse.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.PersonaPulse.personapulse.ui.screens.AnalyticsScreen
import com.PersonaPulse.personapulse.ui.screens.DashboardScreen
import com.PersonaPulse.personapulse.ui.screens.HistoryScreen
import com.PersonaPulse.personapulse.ui.screens.NotificationScreen
import com.PersonaPulse.personapulse.ui.screens.ProfileScreen
import com.PersonaPulse.personapulse.ui.screens.WeatherScreen
import com.PersonaPulse.personapulse.ui.screens.WelcomeScreen
import com.PersonaPulse.personapulse.utils.PreferenceManager

//sets up navigation using a sealed class for route safety
//it has one screen which is the dashboard but it is built to scale.
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Dashboard : Screen("dashboard")
    object Notifications : Screen("notifications")
    object Weather : Screen("weather")
    object History : Screen("history")
    object Analytics : Screen("analytics")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    startDestination: String = Screen.Dashboard.route,
    context: android.content.Context? = null
) {
    val navController = rememberNavController()
    val preferenceManager = remember { context?.let { PreferenceManager(it) } }
    
    // For now, always start with dashboard to debug the issue
    val actualStartDestination = startDestination
    
    NavHost(navController = navController, startDestination = actualStartDestination) {
        composable(Screen.Welcome.route) { 
            WelcomeScreen(
                navController = navController,
                onGetStarted = {
                    preferenceManager?.setWelcomeShown()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Notifications.route) { NotificationScreen(navController) }
        composable(Screen.Weather.route) { WeatherScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
        composable(Screen.Analytics.route) { AnalyticsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
    }
}
