package com.PersonaPulse.personapulse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.PersonaPulse.personapulse.ui.screens.AnalyticsScreen
import com.PersonaPulse.personapulse.ui.screens.DashboardScreen
import com.PersonaPulse.personapulse.ui.screens.HistoryScreen
import com.PersonaPulse.personapulse.ui.screens.NotificationScreen
import com.PersonaPulse.personapulse.ui.screens.ProfileScreen
import com.PersonaPulse.personapulse.ui.screens.WeatherScreen

//sets up navigation using a sealed class for route safety
//it has one screen which is the dashboard but it is built to scale.
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Notifications : Screen("notifications")
    object Weather : Screen("weather")
    object History : Screen("history")
    object Analytics : Screen("analytics")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(startDestination: String = Screen.Dashboard.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Notifications.route) { NotificationScreen(navController) }
        composable(Screen.Weather.route) { WeatherScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
        composable(Screen.Analytics.route) { AnalyticsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
    }
}
