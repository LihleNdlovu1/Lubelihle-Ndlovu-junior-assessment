package com.example.personapulse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personapulse.ui.DashboardScreen


sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
}

@Composable
fun NavGraph(startDestination: String = Screen.Dashboard.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

    }
}
