package com.taffan.storyapp.repository

import com.taffan.storyapp.data.api.ApiService
import com.taffan.storyapp.data.response.GetStoriesResponse
import com.taffan.storyapp.data.response.LoginResponse
import com.taffan.storyapp.data.response.LoginResult
import com.taffan.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterLoginRepository private constructor(
    private val apiService: ApiService
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

    suspend fun getStories(token: String): GetStoriesResponse {
        return withContext(Dispatchers.IO) {
            apiService.getStories()
        }
    }

    companion object {
        @Volatile
        private var instance: RegisterLoginRepository? = null
        fun getInstance(
            apiService: ApiService
        ): RegisterLoginRepository = instance?: synchronized(this) {
            instance?: RegisterLoginRepository(apiService).also {  instance = it }
        }
    }
}