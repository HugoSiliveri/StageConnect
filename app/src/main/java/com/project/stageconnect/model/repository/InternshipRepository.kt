package com.project.stageconnect.model.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.project.stageconnect.model.Internship
import kotlinx.coroutines.tasks.await
import java.io.File

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
     * Charge les détails du dernier stage pour un utilisateur spécifique.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param onInternshipLoaded Callback avec le stage chargé.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getInternshipByUser(userId: String, onInternshipLoaded: (Internship) -> Unit) {
        db.collection("internships").whereEqualTo("userId", userId).whereEqualTo("status", "in_progress").get()
            .addOnSuccessListener { result ->
                val internship = result.documents.firstNotNullOfOrNull { it.toObject(Internship::class.java) }
                if (internship != null) {
                    onInternshipLoaded(internship)
                }
            }
            .addOnFailureListener {
                onInternshipLoaded(Internship())
            }
    }

    /**
     * Charge les détails d'un stage pour un utilisateur et une offre de stage spécifique.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param offerId L'identifiant de l'offre de stage.
     * @param onInternshipLoaded Callback avec le stage chargé.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getInternshipByUserAndOffer(userId: String, offerId: String, onInternshipLoaded: (Internship) -> Unit) {
        db.collection("internships").whereEqualTo("userId", userId).whereEqualTo("offerId", offerId).get()
            .addOnSuccessListener { result ->
                val internship = result.documents.firstNotNullOfOrNull { it.toObject(Internship::class.java) }
                if (internship != null) {
                    onInternshipLoaded(internship)
                }
            }
            .addOnFailureListener {
                onInternshipLoaded(Internship())
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

    /**
     * Modifie l'étape du processus de création de la convention de stage
     *
     * @param internshipId L'identifiant du stage.
     * @param step L'étape du processus.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setStep(internshipId: String, step: Int) {
        db.collection("internships").document(internshipId).update("step", step)
    }

    /**
     * Modifie le nom du fichier de la convention d'un stage.
     *
     * @param internshipId L'identifiant du stage.
     * @param name Le nom du fichier de la convention.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setAgreementName(internshipId: String, name: String) {
        db.collection("internships").document(internshipId).update("agreementName", name)
    }

    /**
     * Modifie le statut d'un stage.
     *
     * @param internshipId L'identifiant du stage.
     * @param status Le statut du stage.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setStatus(internshipId: String, status: String) {
        db.collection("internships").document(internshipId).update("status", status)
    }

    /**
     * Upload la convention d'un stage dans Firebase Storage.
     *
     * @param internshipId L'identifiant du stage.
     * @param fileName Le nom du fichier convention.
     * @param fileUri L'URI du fichier convention.
     * @param onResult Callback avec le résultat de l'opération.
     *
     * @return Un résultat indiquant si l'envoi a réussi ou non.
     */
    fun uploadAgreement(internshipId: String, fileName: String, fileUri: Uri, onResult: (Unit?) -> Unit = {}) {
        val storageRef = FirebaseStorage.getInstance().reference.child("internships/${internshipId}/agreements/${fileName}")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                onResult(Unit)
            }.addOnFailureListener {
                onResult(Unit)
            }
    }


    /**
     * Récupère la convention d'un stage spécifique.
     *
     * @param internshipId L'identifiant du stage.
     * @param fileName Le nom du fichier convention.
     * @param context Le contexte de l'application.
     * @param onResult Callback avec le résultat de l'opération.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun fetchAgreement(internshipId: String, fileName: String, context: Context, onResult: (Unit?) -> Unit = {}) {
        val storageRef = FirebaseStorage.getInstance().reference.child("internships/${internshipId}/agreements/${fileName}")
        val localFile = File(context.cacheDir, fileName)

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    localFile
                )

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                onResult(Unit)
            }
            .addOnFailureListener {
                onResult(Unit)
            }
    }
}