package com.gnitetskiy.courses.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gnitetskiy.courses.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.AvailableCourses.route,
            onClick = {
                navController.navigate(Screen.AvailableCourses.route) {
                    popUpTo(Screen.AvailableCourses.route) { inclusive = true }
                }
            },
            label = { Text("Available") },
            icon = {}
        )
        NavigationBarItem(
            selected = currentRoute == Screen.MyCourses.route,
            onClick = {
                navController.navigate(Screen.MyCourses.route) {
                    popUpTo(Screen.MyCourses.route) { inclusive = true }
                }
            },
            label = { Text("My Courses") },
            icon = {}
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Settings.route,
            onClick = {
                navController.navigate(Screen.Settings.route) {
                    popUpTo(Screen.Settings.route) { inclusive = true }
                }
            },
            label = { Text("Settings") },
            icon = {}
        )
    }
}
