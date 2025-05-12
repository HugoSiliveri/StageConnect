package com.project.stageconnect.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     suspend fun signupUser(
        typeKey: String,
        email: String,
        password: String,
        firstname: String? = null,
        lastname: String? = null,
        name: String? = null,
        phone: String,
        address: String,
        institutionId: String? = null
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID introuvable"))

            val userData = mutableMapOf<String, Any>(
                "type" to typeKey,
                "email" to email,
                "phone" to phone,
                "address" to address,
            )

            when (typeKey) {
                "intern" -> {
                    userData["firstname"] = firstname ?: ""
                    userData["lastname"] = lastname ?: ""
                    userData["institutionId"] = institutionId ?: ""
                }
                "company", "educational" -> {
                    userData["structname"] = name ?: ""
                }
            }

            db.collection("users").document(uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}