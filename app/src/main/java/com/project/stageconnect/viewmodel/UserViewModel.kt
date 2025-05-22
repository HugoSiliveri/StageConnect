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

class UserViewModel : ViewModel() {
    var currentUser: User? = null
    private val userRepository = UserRepository()

    private val _userState = MutableStateFlow<DataResult>(DataResult.Idle)
    val userState: StateFlow<DataResult> = _userState

    fun loadUser(onUserLoaded: (User?) -> Unit = {}, uid: String) {
        userRepository.getUser(uid) { user ->
            onUserLoaded(user)
        }
    }

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

    fun loadUsers(onUsersLoaded: (List<User>) -> Unit = {}, userIds: List<String>) {
        userRepository.getUsers(userIds) { list ->
            onUsersLoaded(list)
        }
    }

    fun loadEducationalInstitutions(onInstitutionsLoaded: (List<User>) -> Unit = {}) {
        userRepository.getEducationalInstitutions { list ->
            onInstitutionsLoaded(list)
        }
    }

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

    fun uploadCv(onResult: (Unit?) -> Unit = {}, userId: String, fileName: String, fileUri: Uri) {
        userRepository.uploadCv(userId, fileName, fileUri) { list ->
            onResult(list)
        }
    }

    fun fetchCv(onResult: (Unit?) -> Unit = {}, userId: String, fileName: String, context: Context) {
        userRepository.fetchCv(userId, fileName, context) { list ->
            onResult(list)
        }
    }
}