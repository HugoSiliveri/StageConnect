package com.project.stageconnect.viewmodel

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

    fun editUser(
        uid: String,
        email: String,
        phone: String,
        address: String,
        firstname: String,
        lastname: String,
        structname: String,
        description: String,
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
                description
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
}