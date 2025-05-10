package com.project.stageconnect.model.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await

class InternshipRepository {
    private val db = Firebase.firestore

     fun getInternships(onResult: (List<Internship>) -> Unit) {
        db.collection("internships").get()
            .addOnSuccessListener { result ->
                val internships = result.documents.mapNotNull { it.toObject(Internship::class.java) }
                onResult(internships)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getCompanyInternships(companyId: String, onResult: (List<Internship>) -> Unit) {
        db.collection("internships").whereEqualTo("companyId", companyId).get()
            .addOnSuccessListener { result ->
                val internships = result.documents.mapNotNull { it.toObject(Internship::class.java) }
                onResult(internships)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    suspend fun createInternship(
        companyId: String,
        companyName: String,
        title: String,
        description: String,
        location: String,
        duration: String
    ): Result<Unit> {
        return try {
            val doc = db.collection("internships").document()
            val internship = Internship(
                id = doc.id,
                companyId = companyId,
                companyName = companyName,
                title = title,
                description = description,
                location = location,
                duration = duration
            )
            doc.set(internship).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}