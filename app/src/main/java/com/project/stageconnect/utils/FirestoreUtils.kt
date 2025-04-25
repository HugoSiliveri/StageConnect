package com.project.stageconnect.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.User

fun getCurrentUser(onUserLoaded: (User?) -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onUserLoaded(null)
    val db = Firebase.firestore

    db.collection("users").document(uid).get()
        .addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            onUserLoaded(user)
        }
        .addOnFailureListener {
            onUserLoaded(null)
        }
}

fun signOut() {
    FirebaseAuth.getInstance().signOut()
}