package com.gnitetskiy.courses

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.gnitetskiy.courses.navigation.AppNavHost

@Composable
fun MyApp() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        val navController = rememberNavController()
        // Вызов NavHost, определённого в NavGraph.kt
        AppNavHost(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyApp() {
    MyApp()
}
