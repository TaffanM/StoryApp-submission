package com.taffan.storyapp.repository

import com.taffan.storyapp.data.api.ApiService
import com.taffan.storyapp.data.response.LoginResponse
import com.taffan.storyapp.data.response.RegisterResponse
import com.taffan.storyapp.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterLoginRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
){
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return withContext(Dispatchers.IO) {
            apiService.register(name, email, password)
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            apiService.login(email, password)
        }
    }


    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ) = RegisterLoginRepository(apiService, userPreferences)
    }
}