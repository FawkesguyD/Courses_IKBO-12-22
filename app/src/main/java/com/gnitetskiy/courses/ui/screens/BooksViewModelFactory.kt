package com.gnitetskiy.courses.ui.screens

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Фабрика для создания BooksViewModel
class BooksViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
