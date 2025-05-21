package com.project.stageconnect.model.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.stageconnect.model.Application
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await

class ApplicationRepository {

    private val db = Firebase.firestore

    fun getApplicationByUserAndInternship(userId: String, internshipId: String, onResult: (Application?) -> Unit) {
        db.collection("applications").whereEqualTo("userId", userId).whereEqualTo("internshipId", internshipId).get()
            .addOnSuccessListener { result ->
                val application = result.documents.firstNotNullOfOrNull { it.toObject(Application::class.java) }
                onResult(application)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

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

    suspend fun cancelApplication(userId: String, internshipId: String): Result<Unit> {
        return try {
            val querySnapshot = db.collection("applications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("internshipId", internshipId)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                db.collection("applications").document(document.id).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInternshipApplications(internshipId: String): Result<Unit> {
        return try {
            val querySnapshot = db.collection("applications")
                .whereEqualTo("internshipId", internshipId)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                db.collection("applications").document(document.id).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}