package com.taffan.storyapp.ui.model

import StoryRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taffan.storyapp.data.response.Story
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repository: StoryRepository): ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getStory(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDetail(id)
                if(!response.error) {
                    _story.value = response.story
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