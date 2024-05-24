package com.taffan.storyapp.di

import android.content.Context
import com.taffan.storyapp.data.api.ApiConfig
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.repository.RegisterLoginRepository
import com.taffan.storyapp.utils.AppExecutors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): RegisterLoginRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = if (user != null ) {
            ApiConfig.getApiService(user.token)
        } else {
            ApiConfig.getApiService("")
        }
        return RegisterLoginRepository.getInstance(apiService, pref)
    }
}