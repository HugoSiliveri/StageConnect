package com.project.stageconnect.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.utils.getCurrentUser
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private var currentUser: User? = null

    fun loadCurrentUser(onUserLoaded: (User?) -> Unit) {
        viewModelScope.launch {
            getCurrentUser { user ->
                currentUser = user
                onUserLoaded(user)
            }
        }
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}