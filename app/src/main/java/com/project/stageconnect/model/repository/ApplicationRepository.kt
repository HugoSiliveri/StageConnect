package com.project.stageconnect.model.repository

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.project.stageconnect.model.Application
import kotlinx.coroutines.tasks.await

/**
 * Repository responsable de la gestion des candidatures (`Application`) dans Firestore.
 *
 * @property db Instance de Firestore.
 */
class ApplicationRepository {

    private val db = Firebase.firestore

    /**
     * Récupère une candidature par son ID.
     *
     * @param applicationId L'ID de la candidature.
     * @param onResult Callback contenant la candidature ou `null` si non trouvée.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getApplication(applicationId: String, onResult: (Application?) -> Unit) {
        db.collection("applications").document(applicationId).get()
            .addOnSuccessListener { document ->
                val application = document.toObject(Application::class.java)
                onResult(application)
            }
    }

    /**
     * Récupère une candidature spécifique à un utilisateur et à une offre de stage.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param internshipId L'identifiant de l'offre de stage.
     * @param onResult Callback avec la candidature ou `null`.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
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

    /**
     * Récupère toutes les candidatures d'une entreprise spécifique.
     *
     * @param companyId L'identifiant de l'entreprise.
     * @param onResult Callback avec la liste des candidatures.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     **/
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

    /**
     * Crée une nouvelle candidature.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param internshipId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la création a réussi ou non.
     */
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

    /**
     * Met à jour le statut d'une candidature à `accepted`.
     *
     * @param applicationId L'identifiant de la candidature.
     *
     * @return `Result`
     */
    suspend fun acceptApplication(applicationId: String): Result<Unit> {
        return try {
            db.collection("applications").document(applicationId).update("status", "accepted").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Met à jour le statut d'une candidature à `denied`.
     *
     * @param applicationId L'identifiant de la candidature.
     *
     * @return `Result`
    **/
    suspend fun denyApplication(applicationId: String): Result<Unit> {
        return try {
            val update = mapOf(
                "status" to "denied",
                "deniedAt" to Timestamp.now()
            )
            db.collection("applications").document(applicationId).update(update).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Annule une candidature spécifique à un utilisateur et à une offre de stage.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param internshipId L'identifiant de l'offre de stage.
     *
     * @return `Result`
     */
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

    /**
     * Supprime toutes les candidatures d'une offre de stage spécifique.
     *
     * @param internshipId L'identifiant de l'offre de stage.
     *
     * @return `Result`
     */
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