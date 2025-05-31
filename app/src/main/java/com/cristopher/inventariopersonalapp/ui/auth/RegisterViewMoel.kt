package com.cristopher.inventariopersonalapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password)
            _registerResult.postValue(result)
        }
    }
}