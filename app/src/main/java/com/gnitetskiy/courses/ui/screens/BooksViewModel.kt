package com.gnitetskiy.courses.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenLibraryResponse(
    val start: Int,
    @SerializedName("num_found") val numFound: Int,
    val docs: List<Book>
)

data class Book(
    val title: String,
    @SerializedName("author_name") val authorName: List<String>?
)

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 50
    ): OpenLibraryResponse
}

class BooksViewModel(application: Application) : AndroidViewModel(application) {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(OpenLibraryApiService::class.java)

    init {
        searchBooks("harry potter")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _errorMessage.value = null
                val response = apiService.searchBooks(query, 50)
                _books.value = response.docs

                if (query.isNotBlank()) {
                    saveSearchHistory(query)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Соединение с сетью потеряно"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun saveSearchHistory(query: String) {
        viewModelScope.launch {
            val prefs = getApplication<Application>()
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
            val history = prefs.getStringSet("search_history", emptySet()) ?: emptySet()

            val newHistory = history.toMutableSet()
            newHistory.add(query)

            prefs.edit().putStringSet("search_history", newHistory).apply()
        }
    }
    fun recordSearchHistory(query: String) {
        if (query.isNotBlank()) {
            viewModelScope.launch {
                val prefs = getApplication<Application>()
                    .getSharedPreferences("settings", Context.MODE_PRIVATE)
                val history = prefs.getStringSet("search_history", emptySet()) ?: emptySet()
                val newHistory = history.toMutableSet().apply { add(query) }
                prefs.edit().putStringSet("search_history", newHistory).apply()
            }
        }
    }

}
