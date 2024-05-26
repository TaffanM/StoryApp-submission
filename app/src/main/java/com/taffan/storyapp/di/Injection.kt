package com.taffan.storyapp.di

import StoryRepository
import android.content.Context
import com.taffan.storyapp.data.api.ApiConfig
import com.taffan.storyapp.data.api.ApiConfigStory
import com.taffan.storyapp.preferences.UserPreferences
import com.taffan.storyapp.preferences.dataStore
import com.taffan.storyapp.repository.RegisterLoginRepository
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

    fun storyProvideRepository(context: Context): StoryRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiServiceStory = ApiConfigStory.getApiService(pref)
        return StoryRepository.getInstance(apiServiceStory, pref)
    }
}