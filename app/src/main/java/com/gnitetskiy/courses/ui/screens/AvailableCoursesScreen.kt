package com.gnitetskiy.courses.ui.screens

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gnitetskiy.courses.ui.components.BottomNavigationBar
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableCoursesScreen(
    onCourseClick: () -> Unit,
    navController: NavHostController,
    viewModel: BooksViewModel = viewModel(
        factory = BooksViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    // Текущее значение поискового запроса
    var searchQuery by remember { mutableStateOf("") }

    // Состояния из ViewModel
    val books by viewModel.books.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    if (errorMessage != null) {
        showErrorDialog = true
    }

    // Работа с SharedPreferences для чтения истории
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // Читаем историю поиска (список строк) и слушаем изменения
    val searchHistory by produceState(
        initialValue = prefs.getStringSet("search_history", emptySet())?.toList() ?: emptyList()
    ) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "search_history") {
                value = prefs.getStringSet("search_history", emptySet())?.toList() ?: emptyList()
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // Флаг, показывать ли выпадающее меню истории
    var showHistoryDropdown by remember { mutableStateOf(false) }

    // Фокус-менеджер, чтобы скрывать клавиатуру и сворачивать меню
    val focusManager = LocalFocusManager.current

    // Фильтр для отображаемых книг
    val displayedBooks = if (searchQuery.isBlank()) books
    else books.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Available Books") })
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
            // Блок для поля ввода + выпадающего меню
            Box(modifier = Modifier.fillMaxWidth()) {

                // Поле ввода
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        // Показываем историю, если что-то введено или поле в фокусе
                        showHistoryDropdown = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            // Когда поле получает фокус, показываем историю
                            showHistoryDropdown = focusState.isFocused
                        },
                    label = { Text("Search Books") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                // Например, сбросим на дефолтный поиск
                                viewModel.searchBooks("harry potter")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        }
                    }
                )

                // Выпадающее меню с историей (или любым списком "подсказок")
                DropdownMenu(
                    expanded = showHistoryDropdown && searchHistory.isNotEmpty(),
                    onDismissRequest = {
                        // При клике вне меню закрываем
                        showHistoryDropdown = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    searchHistory.forEach { historyItem ->
                        DropdownMenuItem(
                            text = { Text(historyItem) },
                            onClick = {
                                searchQuery = historyItem
                                viewModel.searchBooks(historyItem)
                                showHistoryDropdown = false
                                // Спрячем клавиатуру
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }

            // Кнопка "Поиск" (опционально) – или можно искать при каждом вводе
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.searchBooks(searchQuery)
                    // Скрываем меню истории и клавиатуру
                    showHistoryDropdown = false
                    focusManager.clearFocus()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Search")
            }

            // Индикатор загрузки
            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (displayedBooks.isEmpty() && !isLoading) {
                Text(
                    text = "Ничего не найдено",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(displayedBooks) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Заглушка под обложку
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                book.authorName?.let { authors ->
                                    Text(
                                        text = "Автор: ${authors.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                            // При нажатии "Open" – сохраняем запрос и переходим на детали
                            Button(onClick = {
                                viewModel.recordSearchHistory(searchQuery)
                                onCourseClick()
                            }) {
                                Text("Open")
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог ошибки
    if (showErrorDialog && errorMessage != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Ошибка") },
            text = { Text(errorMessage!!) }
        )
    }
}
