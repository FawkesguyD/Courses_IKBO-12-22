package com.gnitetskiy.courses.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gnitetskiy.courses.api.UserApi
import com.gnitetskiy.courses.model.User
import com.gnitetskiy.courses.model.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserResponse) : AuthState()
    data class Error(val message: String) : AuthState()
    object RegistrationSuccess : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // Используем адрес для эмулятора
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val userApi = retrofit.create(UserApi::class.java)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = userApi.loginUser(User(email = email, password = password))
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        prefs.edit().putString("user_email", user.email).apply()
                        _authState.value = AuthState.Success(user)
                    } ?: run {
                        _authState.value = AuthState.Error("Empty response body")
                    }
                } else {
                    _authState.value = AuthState.Error("Неверный email или пароль")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = userApi.registerUser(User(email = email, password = password))
                if (response.isSuccessful) {
                    _authState.value = AuthState.RegistrationSuccess
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
} 