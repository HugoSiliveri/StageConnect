package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.project.stageconnect.model.User
import com.project.stageconnect.model.repository.UserRepository

class UserViewModel : ViewModel() {
    private var currentUser: User? = null
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