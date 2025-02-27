package com.gnitetskiy.courses.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object AvailableCourses : Screen("availableCourses")
    object MyCourses : Screen("myCourses")
    object CourseDetails : Screen("courseDetails")
    object Settings : Screen("settings")
}
