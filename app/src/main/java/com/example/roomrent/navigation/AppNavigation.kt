package com.example.roomrent.navigation


import Dashboard
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.roomrent.ui.screens.Splashscreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") { Splashscreen(navController) }
        composable("Dashboard") { Dashboard(navController) }
    }

}