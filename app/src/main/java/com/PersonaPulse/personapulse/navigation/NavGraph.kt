package com.PersonaPulse.personapulse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.PersonaPulse.personapulse.ui.DashboardScreen

//sets up navigation using a sealed class for route safety
//it has one screen which is the dashboard but it is built to scale.
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
