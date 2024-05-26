package com.taffan.storyapp.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taffan.storyapp.data.response.LoginResponse
import com.taffan.storyapp.repository.RegisterLoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val registerLoginRepository: RegisterLoginRepository): ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = registerLoginRepository.login(email, password)
                if (!response.error) {
                    _loginResponse.postValue(response)
                } else {
                    _error.postValue(response.message)
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
