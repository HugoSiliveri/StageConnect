package com.project.stageconnect.model.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await

/**
 * Repository responsable des offres de stage (`Internship`) dans Firestore.
 *
 * @property db Instance de Firestore.
 */
class InternshipRepository {
    private val db = Firebase.firestore

    /**
     * Récupère une offre de stage par son ID.
     *
     * @param internshipId L'ID de l'offre de stage.
     * @param onResult Callback contenant l'offre de stage ou `null` si non trouvée.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
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

    /**
     * Récupère toutes les offres de stage dont l'utilisateur n'a pas encore réalisé de candidature.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param onResult Callback avec la liste des offres de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
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

    /**
     * Récupère toutes les offres de stage d'une entreprise spécifique.
     *
     * @param companyId L'identifiant de l'entreprise.
     * @param onResult Callback avec la liste des offres de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getCompanyInternships(companyId: String, onResult: (List<Internship>) -> Unit) {
        db.collection("internships").whereEqualTo("companyId", companyId).get()
            .addOnSuccessListener { result ->
                val internships = result.documents.mapNotNull { it.toObject(Internship::class.java) }
                onResult(internships)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    /**
     * Récupère toutes les offres de stage d'une entreprise spécifique.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param onResult Callback avec la liste des offres de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
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

    /**
     * Crée une nouvelle offre de stage.
     *
     * @param companyId L'identifiant de l'entreprise.
     * @param companyName Le nom de l'entreprise.
     * @param title Le titre de l'offre de stage.
     * @param description La description de l'offre de stage.
     * @param location La localisation de l'offre de stage.
     * @param duration La durée de l'offre de stage.
     *
     * @return Un résultat indiquant si la création a réussi ou non.
     */
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

    /**
     * Supprime une offre de stage.
     *
     * @param internshipId L'identifiant de l'offre de stage à supprimer.
     *
     * @return Un résultat indiquant si la suppression a réussi ou non.
     */
    suspend fun deleteInternship(internshipId: String): Result<Unit> {
        return try {
            db.collection("internships").document(internshipId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}