package com.gnitetskiy.courses.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gnitetskiy.courses.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    // Пример перехода на экран доступных курсов
                    navController.navigate(Screen.AvailableCourses.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AvailableCourses.route) {
            AvailableCoursesScreen(
                onCourseClick = { navController.navigate(Screen.CourseDetails.route) },
                navController = navController
            )
        }
        composable(Screen.MyCourses.route) {
            MyCoursesScreen(navController = navController)
        }
        composable(Screen.CourseDetails.route) {
            CourseDetailsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
