package com.taffan.storyapp.ui.model

import StoryRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taffan.storyapp.di.Injection


class StoryViewModelFactory(
    private val storyRepository: StoryRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            StoryViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            DetailStoryViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)){
            MapsViewModel(storyRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = StoryViewModelFactory(Injection.storyProvideRepository(context.applicationContext))
    }
}