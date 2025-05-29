package com.gnitetskiy.courses.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gnitetskiy.courses.ui.screens.LoginScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.AvailableCourses.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AvailableCourses.route) {
            // Здесь будет экран доступных курсов
        }
        composable(Screen.MyCourses.route) {
            // Здесь будет экран моих курсов
        }
        composable(Screen.CourseDetails.route) {
            // Здесь будет экран деталей курса
        }
        composable(Screen.Settings.route) {
            // Здесь будет экран настроек
        }
    }
} 