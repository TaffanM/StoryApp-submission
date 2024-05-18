package com.taffan.storyapp.di

import android.content.Context
import com.taffan.storyapp.data.api.ApiConfig
import com.taffan.storyapp.repository.RegisterLoginRepository
import com.taffan.storyapp.utils.AppExecutors

object Injection {
    fun provideRepository(): RegisterLoginRepository {
        val apiService = ApiConfig.getApiService()
        return RegisterLoginRepository.getInstance(apiService)
    }
}