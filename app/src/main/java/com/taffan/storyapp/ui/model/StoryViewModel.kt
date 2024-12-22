package com.taffan.storyapp.ui.model

import StoryRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taffan.storyapp.data.response.ListStoryItem

class StoryViewModel(private val repository: StoryRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    val storiesPaging: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesPaging()
            .cachedIn(viewModelScope)

    init {
        observeLoadingState()
    }

    private fun observeLoadingState() {
        storiesPaging.observeForever {
            _error.value = ""
            _isLoading.value = false
        }
    }
}