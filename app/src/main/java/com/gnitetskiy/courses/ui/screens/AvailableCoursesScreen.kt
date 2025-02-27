package com.gnitetskiy.courses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gnitetskiy.courses.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableCoursesScreen(
    onCourseClick: () -> Unit,
    navController: NavHostController
) {
    val courses = listOf(
        "Course 1", "Course 2", "Course 3",
        "Course 4", "Course 5", "Course 6"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Available Courses") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(courses) { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Заглушка для картинки
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = course,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Button(onClick = onCourseClick) {
                        Text("Open")
                    }
                }
            }
        }
    }
}
