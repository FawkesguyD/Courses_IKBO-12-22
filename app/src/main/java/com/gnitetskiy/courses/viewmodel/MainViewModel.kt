package com.gnitetskiy.courses.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    // Пример: состояние пользователя (авторизован/не авторизован)
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    fun login(email: String, password: String) {
        // Реализуйте логику входа
        // При успехе:
        _isUserLoggedIn.value = true
    }

    fun logout() {
        _isUserLoggedIn.value = false
    }
}
