package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _signupState = MutableStateFlow<DataResult>(DataResult.Idle)
    val signupState: StateFlow<DataResult> = _signupState

    fun signup(
        typeKey: String,
        email: String,
        password: String,
        firstname: String? = null,
        lastname: String? = null,
        name: String? = null,
        phone: String,
        address: String,
        institutionId: String? = null
    ) {
        viewModelScope.launch {
            _signupState.value = DataResult.Loading
            val result = authRepository.signupUser(
                typeKey,
                email,
                password,
                firstname,
                lastname,
                name,
                phone,
                address,
                institutionId
            )
            _signupState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}