package com.project.stageconnect.model.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.stageconnect.model.Offer
import kotlinx.coroutines.tasks.await

/**
 * Repository responsable des offres de stage (`Offer`) dans Firestore.
 *
 * @property db Instance de Firestore.
 */
class OfferRepository {
    private val db = Firebase.firestore

    /**
     * Récupère une offre de stage par son ID.
     *
     * @param offerId L'ID de l'offre de stage.
     * @param onResult Callback contenant l'offre de stage ou `null` si non trouvée.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getOffer(offerId: String, onResult: (Offer?) -> Unit) {
        db.collection("offers").document(offerId).get()
            .addOnSuccessListener { document ->
                val offer = document.toObject(Offer::class.java)
                onResult(offer)
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
    fun getNoApplicationOffers(userId: String, onResult: (List<Offer>) -> Unit) {
        db.collection("applications").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val appliedOfferIds = result.documents.mapNotNull { it.getString("offerId") }
                if (appliedOfferIds.isEmpty()) {
                    db.collection("offers").get()
                        .addOnSuccessListener { offerResult ->
                            val offers = offerResult.documents.mapNotNull { it.toObject(Offer::class.java) }
                            onResult(offers)
                        }
                        .addOnFailureListener { onResult(emptyList()) }
                } else {
                    db.collection("offers")
                        .whereNotIn("id", appliedOfferIds)
                        .get()
                        .addOnSuccessListener { offerResult ->
                            val offers = offerResult.documents.mapNotNull { it.toObject(Offer::class.java) }
                            onResult(offers)
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
    fun getCompanyOffers(companyId: String, onResult: (List<Offer>) -> Unit) {
        db.collection("offers").whereEqualTo("companyId", companyId).get()
            .addOnSuccessListener { result ->
                val offers = result.documents.mapNotNull { it.toObject(Offer::class.java) }
                onResult(offers)
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
    fun getApplicationOffers(userId: String, onResult: (List<Offer>) -> Unit) {
        db.collection("applications").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val offerId = result.documents.mapNotNull { it.getString("offerId") }
                if (offerId.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                db.collection("offers").whereIn("id", offerId).get()
                    .addOnSuccessListener { offerResult ->
                        val offers = offerResult.documents.mapNotNull { it.toObject(Offer::class.java) }
                        onResult(offers)
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
    suspend fun createOffer(
        companyId: String,
        companyName: String,
        title: String,
        description: String,
        location: String,
        duration: String
    ): Result<Unit> {
        return try {
            val doc = db.collection("offers").document()
            val offer = Offer(
                id = doc.id,
                companyId = companyId,
                companyName = companyName,
                title = title,
                description = description,
                location = location,
                duration = duration
            )
            doc.set(offer).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Supprime une offre de stage.
     *
     * @param offerId L'identifiant de l'offre de stage à supprimer.
     *
     * @return Un résultat indiquant si la suppression a réussi ou non.
     */
    suspend fun deleteOffer(offerId: String): Result<Unit> {
        return try {
            db.collection("offers").document(offerId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}