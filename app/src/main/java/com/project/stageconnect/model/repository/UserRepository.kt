package com.project.stageconnect.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.User

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    user.uid = document.id
                }
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun signOut() {
        auth.signOut()
    }
}