package com.project.stageconnect.model.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.stageconnect.model.Application
import kotlinx.coroutines.tasks.await

class ApplicationRepository {

    private val db = Firebase.firestore

    suspend fun createApplication(userId: String, internshipId: String): Result<Unit> {
        return try {
            val doc = db.collection("applications").document()
            val application = Application(
                id = doc.id,
                userId = userId,
                internshipId = internshipId,
                status = "pending"
            )
            doc.set(application).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}