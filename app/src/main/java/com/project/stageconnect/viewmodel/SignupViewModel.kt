package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.stageconnect.model.repository.AuthRepository
import com.project.stageconnect.ui.auth.SignupResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _signupState = MutableStateFlow<SignupResult>(SignupResult.Idle)
    val signupState: StateFlow<SignupResult> = _signupState

    fun signup(
        typeKey: String,
        email: String,
        password: String,
        firstname: String? = null,
        lastname: String? = null,
        name: String? = null,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _signupState.value = SignupResult.Loading
            val result = authRepository.signupUser(
                typeKey,
                email,
                password,
                firstname,
                lastname,
                name,
                phone,
                address
            )
            _signupState.value = if (result.isSuccess) {
                SignupResult.Success
            } else {
                SignupResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}