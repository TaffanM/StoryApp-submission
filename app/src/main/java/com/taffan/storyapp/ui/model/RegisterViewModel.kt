package com.taffan.storyapp.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taffan.storyapp.data.response.RegisterResponse
import com.taffan.storyapp.repository.RegisterLoginRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerLoginRepository: RegisterLoginRepository): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = registerLoginRepository.register(name, email, password)
                if (!response.error) {
                    _registerResponse.postValue(response)
                } else {
                    _error.postValue("Registration Failed : ${response.message}")
                }
            } catch (e: Exception) {
                _error.postValue("Registration Failed : ${e.message}")
            }
        }
    }

}