package com.project.stageconnect.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private var currentUser: User? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun loadCurrentUser(onUserLoaded: (User?) -> Unit) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid == null) {
                onUserLoaded(null)
                return@launch
            }

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    currentUser = user
                    onUserLoaded(user)
                }
                .addOnFailureListener {
                    onUserLoaded(null)
                }
        }
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }
}