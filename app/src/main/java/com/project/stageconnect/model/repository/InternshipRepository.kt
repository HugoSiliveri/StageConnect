package com.project.stageconnect.model.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await

class InternshipRepository {
    private val db = Firebase.firestore

    fun getInternship(internshipId: String, onResult: (Internship?) -> Unit) {
        db.collection("internships").document(internshipId).get()
            .addOnSuccessListener { document ->
                val internship = document.toObject(Internship::class.java)
                onResult(internship)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getNoApplicationInternships(userId: String, onResult: (List<Internship>) -> Unit) {
        db.collection("applications").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val appliedInternshipIds = result.documents.mapNotNull { it.getString("internshipId") }
                if (appliedInternshipIds.isEmpty()) {
                    db.collection("internships").get()
                        .addOnSuccessListener { internshipResult ->
                            val internships = internshipResult.documents.mapNotNull { it.toObject(Internship::class.java) }
                            onResult(internships)
                        }
                        .addOnFailureListener { onResult(emptyList()) }
                } else {
                    db.collection("internships")
                        .whereNotIn("id", appliedInternshipIds)
                        .get()
                        .addOnSuccessListener { internshipResult ->
                            val internships = internshipResult.documents.mapNotNull { it.toObject(Internship::class.java) }
                            onResult(internships)
                        }
                        .addOnFailureListener { onResult(emptyList()) }
                }
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

    fun getApplicationInternships(userId: String, onResult: (List<Internship>) -> Unit) {
        db.collection("applications").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val internshipId = result.documents.mapNotNull { it.getString("internshipId") }
                db.collection("internships").whereIn("id", internshipId).get()
                    .addOnSuccessListener { internshipResult ->
                        val internships = internshipResult.documents.mapNotNull { it.toObject(Internship::class.java) }
                        onResult(internships)
                    }
                    .addOnFailureListener { onResult(emptyList()) }
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

    suspend fun deleteInternship(internshipId: String): Result<Unit> {
        return try {
            db.collection("internships").document(internshipId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}