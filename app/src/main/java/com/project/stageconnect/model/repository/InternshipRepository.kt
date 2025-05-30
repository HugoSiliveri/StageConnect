package com.project.stageconnect.model.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await

/**
 * Repository responsable des données en lien avec les stages (`Internship`).
 *
 * @property db Instance de Firestore.
 */
class InternshipRepository {

    private val db = Firebase.firestore

    /**
     * Charge les détails d'un stage spécifique.
     *
     * @param internshipId L'identifiant du stage.
     * @param onInternshipLoaded Callback avec le stage chargé.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getInternship(internshipId: String, onInternshipLoaded: (Internship) -> Unit) {
        db.collection("internships").document(internshipId).get()
            .addOnSuccessListener { document ->
                val internship = document.toObject(Internship::class.java)
                if (internship != null) {
                    onInternshipLoaded(internship)
                }
            }
            .addOnFailureListener {
                onInternshipLoaded(Internship())
            }
    }

    /**
     * Charge les stages associés aux étudiants.
     *
     * @param userIds Liste des identifiants des étudiants.
     * @param onInternshipsLoaded Callback avec la liste des stages.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getUsersInternships(userIds: List<String>, onInternshipsLoaded: (List<Internship>) -> Unit) {
        if (userIds.isEmpty()) {
            onInternshipsLoaded(emptyList())
            return
        }

        val chunks = userIds.chunked(10)
        val internships = mutableListOf<Internship>()
        var remaining = chunks.size

        for (chunk in chunks) {
            db.collection("internships").whereIn("userId", chunk).get()
                .addOnSuccessListener { result ->
                    val part = result.documents.mapNotNull { it.toObject(Internship::class.java) }
                    internships.addAll(part)
                    remaining--
                    if (remaining == 0) {
                        onInternshipsLoaded(internships)
                    }
                }
                .addOnFailureListener {
                    onInternshipsLoaded(emptyList())
                    return@addOnFailureListener
                }
        }
    }

    /**
     * Crée un nouveau stage pour un utilisateur et une offre de stage spécifique.
     *
     * @param offerId L'identifiant de l'offre de stage.
     * @param userId L'identifiant de l'utilisateur.
     */
    suspend fun createInternship(offerId: String, userId: String): Result<Unit> {
        return try {
            val doc = db.collection("internships").document()
            val internship = Internship(
                id = doc.id,
                offerId = offerId,
                userId = userId,
                status = "not_started"
            )
            doc.set(internship).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}