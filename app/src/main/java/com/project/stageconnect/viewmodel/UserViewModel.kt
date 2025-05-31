package com.project.stageconnect.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable des utilisateurs (`User`).
 *
 * @property currentUser L'utilisateur actuellement connecté.
 * @property userRepository Repository d'utilisateurs.
 * @property _userState Etat de l'utilisateur.
 * @property userState Etat de l'utilisateur sous forme de StateFlow.
 */
class UserViewModel : ViewModel() {
    var currentUser: User? = null
    private val userRepository = UserRepository()

    private val _userState = MutableStateFlow<DataResult>(DataResult.Idle)
    val userState: StateFlow<DataResult> = _userState

    /**
     * Charge les informations d'un utilisateur.
     *
     * @param onUserLoaded Callback avec les informations de l'utilisateur.
     * @param uid L'ID de l'utilisateur.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadUser(onUserLoaded: (User?) -> Unit = {}, uid: String) {
        userRepository.getUser(uid) { user ->
            onUserLoaded(user)
        }
    }

    /**
     * Charge les informations de l'utilisateur actuellement connecté.
     *
     * @param onUserLoaded Callback avec les informations de l'utilisateur.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadCurrentUser(onUserLoaded: (User?) -> Unit = {}) {
        val uid = userRepository.getCurrentUserId()
        if (uid == null) {
            onUserLoaded(null)
            return
        }

        userRepository.getUser(uid) { user ->
            currentUser = user
            onUserLoaded(user)
        }
    }

    /**
     * Charge les informations de plusieurs utilisateurs.
     *
     * @param onUsersLoaded Callback avec la liste des utilisateurs.
     * @param userIds Liste des ID d'utilisateurs.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadUsers(onUsersLoaded: (List<User>) -> Unit = {}, userIds: List<String>) {
        userRepository.getUsers(userIds) { list ->
            onUsersLoaded(list)
        }
    }

    /**
     * Charge les établissements (`educational`).
     *
     * @param onInstitutionsLoaded Callback avec la liste des établissements.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadEducationalInstitutions(onInstitutionsLoaded: (List<User>) -> Unit = {}) {
        userRepository.getEducationalInstitutions { list ->
            onInstitutionsLoaded(list)
        }
    }

    /**
     * Charge les étudiants (`student`) de l'établissement spécifié.
     *
     * @param onStudentsLoaded Callback avec la liste des étudiants.
     * @param institutionId L'ID de l'établissement.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadStudents(onStudentsLoaded: (List<User>) -> Unit = {}, institutionId: String) {
        userRepository.getStudents(institutionId) { list ->
            onStudentsLoaded(list)
        }
    }

    /**
     * Charge les stagiaires (`intern`) de l'établissement spécifié.
     *
     * @param onStudentsLoaded Callback avec la liste des stagiaires.
     * @param institutionId L'ID de l'établissement.
     *
     * @return Un résultat indiquant si le chargement a réussi ou non.
     */
    fun loadInterns(onStudentsLoaded: (List<User>) -> Unit = {}, companyId: String) {
        userRepository.getInterns(companyId) { list ->
            onStudentsLoaded(list)
        }

    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param uid L'ID de l'utilisateur.
     * @param email L'adresse e-mail de l'utilisateur.
     * @param phone Le numéro de téléphone de l'utilisateur.
     * @param address L'adresse de l'utilisateur.
     * @param firstname Le prénom de l'utilisateur (pour les étudiants).
     * @param lastname Le nom de famille de l'utilisateur (pour les étudiants).
     * @param structname Le nom de l'entreprise ou de l'établissement (pour les entreprises et établissements).
     * @param description La description de l'utilisateur.
     * @param institutionId L'identifiant de l'établissement de l'utilisateur (pour les étudiants).
     * @param cvName Le nom du fichier CV de l'utilisateur.
     *
     * @return Un résultat indiquant si la mise à jour a réussi ou non.
     */
    fun editUser(
        uid: String,
        email: String,
        phone: String,
        address: String,
        firstname: String,
        lastname: String,
        structname: String,
        description: String,
        institutionId: String,
        cvName: String
    ) {
        viewModelScope.launch {
            _userState.value = DataResult.Loading
            val result = userRepository.editUser(
                uid,
                email,
                phone,
                address,
                firstname,
                lastname,
                structname,
                description,
                institutionId,
                cvName
            )
            _userState.value = if (result.isSuccess) {
                loadCurrentUser()
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Déconnecte l'utilisateur.
     *
     * @return Un résultat indiquant si la déconnexion a réussi ou non.
     */
    fun signOut() {
        viewModelScope.launch {
            _userState.value = DataResult.Loading
            val result = userRepository.signOut()
            if (result.isSuccess) {
                currentUser = null
                _userState.value = DataResult.Success
            } else {
                _userState.value = DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Upload un CV d'un utilisateur dans Firebase Storage.
     *
     * @param onResult Callback avec le résultat de l'opération.
     * @param userId L'ID de l'utilisateur.
     * @param fileName Le nom du fichier CV.
     * @param fileUri L'URI du fichier CV.
     *
     * @return Un résultat indiquant si l'envoi a réussi ou non.
     */
    fun uploadCv(onResult: (Unit?) -> Unit = {}, userId: String, fileName: String, fileUri: Uri) {
        userRepository.uploadCv(userId, fileName, fileUri) { list ->
            onResult(list)
        }
    }


    /**
     * Récupère un CV d'un utilisateur spécifique.
     *
     * @param onResult Callback avec le résultat de l'opération.
     * @param userId L'ID de l'utilisateur.
     * @param fileName Le nom du fichier CV.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun fetchCv(onResult: (Unit?) -> Unit = {}, userId: String, fileName: String, context: Context) {
        userRepository.fetchCv(userId, fileName, context) { list ->
            onResult(list)
        }
    }
}