package com.taffan.storyapp.ui.model

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taffan.storyapp.di.Injection
import com.taffan.storyapp.repository.RegisterLoginRepository


class ViewModelFactory(
    private val registerLoginRepository: RegisterLoginRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            RegisterViewModel(registerLoginRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(registerLoginRepository) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            StoryViewModel(registerLoginRepository) as T
        } else if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)){
            DetailStoryViewModel(registerLoginRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}