package com.gnitetskiy.courses.model

data class User(
    val id: Int? = null,
    val email: String,
    val password: String
)

data class UserResponse(
    val id: Int,
    val email: String
) 