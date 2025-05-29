package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la logique de connexion (`login`).
 *
 * @property authRepository Repository d'authentification.
 * @property _loginState Etat de la connexion.
 * @property loginState Etat de la connexion sous forme de StateFlow.
 */
class LoginViewModel() : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _loginState = MutableStateFlow<DataResult>(DataResult.Idle)
    val loginState: StateFlow<DataResult> = _loginState

    /**
     * Effectue la connexion de l'utilisateur.
     *
     * @param email Adresse e-mail de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     *
     * @return Un résultat indiquant si la connexion a réussi ou non.
     */
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
