package com.gnitetskiy.courses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.SearchBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gnitetskiy.courses.ui.components.BottomNavigationBar
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Модели данных Open Library API
data class OpenLibraryResponse(
    val start: Int,
    @SerializedName("num_found") val numFound: Int,
    val docs: List<Book>
)

data class Book(
    val title: String,
    @SerializedName("author_name") val authorName: List<String>?
)

// Retrofit-интерфейс с параметром limit для ограничения результатов до 50 книг
interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): OpenLibraryResponse
}

// ViewModel для загрузки данных из API
class BooksViewModel : ViewModel() {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(OpenLibraryApiService::class.java)

    init {
        // Загрузка данных при запуске экрана с дефолтным запросом
        searchBooks("harry potter")
    }

    // Метод для выполнения запроса к API
    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val response = apiService.searchBooks(query, limit = 50)
                _books.value = response.docs
            } catch (e: Exception) {
                _errorMessage.value = "Соединение с сетью потеряно"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableCoursesScreen(
    onCourseClick: () -> Unit,
    navController: NavHostController,
    viewModel: BooksViewModel = viewModel()
) {
    // Состояние текста поиска
    var searchQuery by remember { mutableStateOf("") }
    // Состояния из ViewModel
    val books by viewModel.books.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    if (errorMessage != null) {
        showErrorDialog = true
    }

    // Локальное фильтрование списка по введённому запросу
    val displayedBooks = if (searchQuery.isBlank()) books
    else books.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Available Books") }
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
            // SearchBar в компактном режиме
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { viewModel.searchBooks(searchQuery) },
                active = false,
                onActiveChange = {},
                placeholder = { Text("Search Books") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            // Можно также перезагрузить данные с дефолтным запросом
                            viewModel.searchBooks("harry potter")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Дополнительный контент для развёрнутого режима не используется
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (displayedBooks.isEmpty()) {
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(displayedBooks) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Заглушка для обложки
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
                            Button(onClick = onCourseClick) {
                                Text("Open")
                            }
                        }
                    }
                }
            }
        }
    }

    // AlertDialog при ошибке сети
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
