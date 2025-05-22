package com.project.stageconnect.model.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.stageconnect.model.Application
import kotlinx.coroutines.tasks.await

class ApplicationRepository {

    private val db = Firebase.firestore

    fun getApplication(applicationId: String, onResult: (Application?) -> Unit) {
        db.collection("applications").document(applicationId).get()
            .addOnSuccessListener { document ->
                val application = document.toObject(Application::class.java)
                onResult(application)
            }
    }

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

    fun getCompanyApplications(companyId: String, onResult: (List<Application>) -> Unit) {
        db.collection("internships").whereEqualTo("companyId", companyId).get()
            .addOnSuccessListener { internshipResult ->
                val internshipIds = internshipResult.documents.mapNotNull { it.id }
                if (internshipIds.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val chunks = internshipIds.chunked(10)
                val allApplications = mutableListOf<Application>()
                var completedChunks = 0

                for (chunk in chunks) {
                    db.collection("applications").whereIn("internshipId", chunk).get()
                        .addOnSuccessListener { appResult ->
                            val applications = appResult.documents.mapNotNull { it.toObject(Application::class.java) }
                            allApplications.addAll(applications)

                            completedChunks++
                            if (completedChunks == chunks.size) {
                                onResult(allApplications)
                            }
                        }
                        .addOnFailureListener {
                            completedChunks++
                            if (completedChunks == chunks.size) {
                                onResult(allApplications)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
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