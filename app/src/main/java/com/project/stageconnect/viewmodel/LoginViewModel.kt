package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _loginState = MutableStateFlow<DataResult>(DataResult.Idle)
    val loginState: StateFlow<DataResult> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = DataResult.Loading

            val result = authRepository.login(email, password)
            _loginState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}
