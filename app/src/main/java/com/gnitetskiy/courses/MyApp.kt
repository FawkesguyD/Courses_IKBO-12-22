package com.gnitetskiy.courses

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.gnitetskiy.courses.navigation.AppNavHost
import com.gnitetskiy.courses.ui.theme.CoursesTheme

@Composable
fun MyApp() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val themePreference by produceState(
        initialValue = sharedPrefs.getString("theme_preference", "system") ?: "system"
    ) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "theme_preference") {
                value = prefs.getString("theme_preference", "system") ?: "system"
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        awaitDispose {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val darkTheme = when (themePreference) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    CoursesTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost(navController = navController)
        }
    }
}
