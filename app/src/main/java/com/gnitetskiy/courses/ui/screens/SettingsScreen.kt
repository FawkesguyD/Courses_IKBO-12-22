package com.gnitetskiy.courses.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gnitetskiy.courses.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "About the App",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Here you can put some information about the application.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Выпадающий список для выбора темы
            ThemeDropdown()

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Логика выхода из аккаунта */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
fun ThemeDropdown() {
    val context = LocalContext.current
    // Получаем SharedPreferences
    val sharedPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    // Читаем сохранённую тему – по умолчанию "system"
    var currentTheme by remember {
        mutableStateOf(sharedPrefs.getString("theme_preference", "system") ?: "system")
    }

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("system", "light", "dark")
    val labels = mapOf("system" to "System Default", "light" to "Light", "dark" to "Dark")

    Box {
        OutlinedTextField(
            value = labels[currentTheme] ?: "System Default",
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Theme")
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labels[option] ?: option) },
                    onClick = {
                        expanded = false
                        // Сохраняем выбранный вариант в SharedPreferences
                        sharedPrefs.edit().putString("theme_preference", option).apply()
                        currentTheme = option
                    }
                )
            }
        }
    }
}
