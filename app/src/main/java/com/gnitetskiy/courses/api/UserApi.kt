package com.gnitetskiy.courses.api

import com.gnitetskiy.courses.model.User
import com.gnitetskiy.courses.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @POST("api/users")
    suspend fun registerUser(@Body user: User): Response<UserResponse>

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserResponse>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserResponse>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<UserResponse>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    @POST("api/users/login")
    suspend fun loginUser(@Body user: User): Response<UserResponse>
} 