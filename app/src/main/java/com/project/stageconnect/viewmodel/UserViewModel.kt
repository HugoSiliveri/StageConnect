package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.User
import com.project.stageconnect.model.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private var currentUser: User? = null
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    fun loadCurrentUser(onUserLoaded: (User?) -> Unit) {
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

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun signOut() {
        userRepository.signOut()
        currentUser = null
    }
}