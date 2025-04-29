package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.repository.AuthRepository
import com.project.stageconnect.ui.auth.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)

    val loginState: StateFlow<LoginResult> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading

            val result = authRepository.login(email, password)
            _loginState.value = if (result.isSuccess) {
                LoginResult.Success
            } else {
                LoginResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}
