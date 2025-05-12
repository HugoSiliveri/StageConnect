package com.project.stageconnect.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.User
import kotlinx.coroutines.tasks.await

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

    fun getEducationalInstitutions(onResult: (List<User>) -> Unit) {
        db.collection("users").whereEqualTo("type", "educational").get()
            .addOnSuccessListener { result ->
                val institutions = result.mapNotNull { doc ->
                    val institution = doc.toObject(User::class.java)
                    institution.uid = doc.id
                    institution
                }
                onResult(institutions)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    suspend fun editUser(
        uid: String,
        email: String,
        phone: String,
        address: String,
        firstname: String,
        lastname: String,
        structname: String,
        description: String,
        institutionId: String
    ): Result<Unit> {
        return try {
            db.collection("users").document(uid).update(
                mapOf(
                    "email" to email,
                    "phone" to phone,
                    "address" to address,
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "structname" to structname,
                    "description" to description,
                    "institutionId" to institutionId
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}