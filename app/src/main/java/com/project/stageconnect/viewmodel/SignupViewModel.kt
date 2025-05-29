package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la création d'un compte (`signup`).
 *
 * @property authRepository Repository d'authentification.
 * @property _signupState Etat de la création de compte.
 * @property signupState Etat de la création de compte sous forme de StateFlow.
 */
class SignupViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()
    private val _signupState = MutableStateFlow<DataResult>(DataResult.Idle)
    val signupState: StateFlow<DataResult> = _signupState

    /**
     * Effectue la création de compte de l'utilisateur.
     *
     * @param typeKey Clé du type d'utilisateur (intern, company, educational).
     * @param email Adresse e-mail de l'utilisateur.
     * @param password Mot de passe de l'utilisateur.
     * @param firstname Prénom de l'utilisateur (pour les étudiants).
     * @param lastname Nom de famille de l'utilisateur (pour les étudiants).
     * @param name Nom de l'entreprise ou de l'établissement (pour les entreprises et établissements).
     * @param phone Numéro de téléphone de l'utilisateur.
     * @param address Adresse de l'utilisateur.
     * @param institutionId Identifiant de l'établissement de l'utilisateur (pour les étudiants).
     */
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