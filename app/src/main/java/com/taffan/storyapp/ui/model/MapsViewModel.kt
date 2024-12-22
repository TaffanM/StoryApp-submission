package com.taffan.storyapp.ui.model

import StoryRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taffan.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStoriesWithLocation()
                if(!response.error) {
                    _stories.value = response.listStory
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}