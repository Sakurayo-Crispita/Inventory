package com.cristopher.inventariopersonalapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristopher.inventariopersonalapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> = _loginResult

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    init {
        checkUserLoggedIn()
    }

    fun login(email: String, password: String) {

        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _loginResult.postValue(result)
        }
    }

    private fun checkUserLoggedIn() {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
    }
}
