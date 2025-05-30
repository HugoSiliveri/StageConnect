package com.project.stageconnect.model.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.project.stageconnect.model.User
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * Repository responsable des utilisateurs (`User`) dans Firestore.
 *
 * @property auth Instance de FirebaseAuth.
 */
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    /**
     * Récupère l'ID de l'utilisateur actuellement connecté.
     *
     * @return L'ID de l'utilisateur ou `null` si non connecté.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Récupère les informations d'un utilisateur par son ID.
     *
     * @param uid L'ID de l'utilisateur.
     * @param onResult Callback contenant les informations de l'utilisateur ou `null` si non trouvé.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    user.uid = document.id
                }
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Récupère les informations de plusieurs utilisateurs par leurs ID.
     *
     * @param userIds Liste des ID d'utilisateurs.
     * @param onResult Callback avec la liste des utilisateurs.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getUsers(userIds: List<String>, onResult: (List<User>) -> Unit) {

        val chunkedIds = userIds.chunked(10)
        val allUsers = mutableListOf<User>()
        var completedRequests = 0

        for (chunk in chunkedIds) {
            db.collection("users").whereIn(FieldPath.documentId(), chunk).get()
                .addOnSuccessListener { result ->
                    val users = result.documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)
                        user?.apply { uid = doc.id }
                    }
                    allUsers.addAll(users)
                    completedRequests++
                    if (completedRequests == chunkedIds.size) {
                        onResult(allUsers)
                    }
                }
                .addOnFailureListener { e ->
                    completedRequests++
                    if (completedRequests == chunkedIds.size) {
                        onResult(allUsers)  // on renvoie ce qu'on a, même si incomplet
                    }
                }
        }
    }

    /**
     * Récupère toutes les établissements.
     *
     * @param onResult Callback avec la liste des établissements.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getEducationalInstitutions(onResult: (List<User>) -> Unit) {
        db.collection("users").whereEqualTo("type", "educational").get()
            .addOnSuccessListener { result ->
                val institutions = result.mapNotNull { doc ->
                    val institution = doc.toObject(User::class.java)
                    institution.uid = doc.id
                    institution
                }
                onResult(institutions)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Récupère les étudiants d'un établissement spécifique.
     *
     * @param institutionId L'ID de l'établissement.
     * @param onResult Callback avec la liste des étudiants.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun getStudents(institutionId: String, onResult: (List<User>) -> Unit) {
        db.collection("users").whereEqualTo("type", "intern").whereEqualTo("institutionId", institutionId).get()
            .addOnSuccessListener { result ->
                val students = result.mapNotNull { doc ->
                    val student = doc.toObject(User::class.java)
                    student.uid = doc.id
                    student
                    }
                onResult(students)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param uid L'ID de l'utilisateur.
     * @param email L'adresse e-mail de l'utilisateur.
     * @param phone Le numéro de téléphone de l'utilisateur.
     * @param address L'adresse de l'utilisateur.
     * @param firstname Le prénom de l'utilisateur (pour les étudiants).
     * @param lastname Le nom de famille de l'utilisateur (pour les étudiants).
     * @param structname Le nom de l'entreprise ou de l'établissement (pour les entreprises et établissements).
     * @param description La description de l'utilisateur.
     * @param institutionId L'identifiant de l'établissement de l'utilisateur (pour les étudiants).
     * @param cvName Le nom du fichier CV de l'utilisateur.
     *
     * @return Un résultat indiquant si la mise à jour a réussi ou non.
     */
    suspend fun editUser(
        uid: String,
        email: String,
        phone: String,
        address: String,
        firstname: String,
        lastname: String,
        structname: String,
        description: String,
        institutionId: String,
        cvName: String
    ): Result<Unit> {
        return try {
            db.collection("users").document(uid).update(
                mapOf(
                    "email" to email,
                    "phone" to phone,
                    "address" to address,
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "structname" to structname,
                    "description" to description,
                    "institutionId" to institutionId,
                    "cvName" to cvName
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Déconnecte l'utilisateur.
     *
     * @return Un résultat indiquant si la déconnexion a réussi ou non.
     */
    fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Upload un CV d'un utilisateur dans Firebase Storage.
     *
     * @param userId L'ID de l'utilisateur.
     * @param fileName Le nom du fichier CV.
     * @param fileUri L'URI du fichier CV.
     * @param onResult Callback avec le résultat de l'opération.
     *
     * @return Un résultat indiquant si l'envoi a réussi ou non.
     */
    fun uploadCv(userId: String, fileName: String, fileUri: Uri, onResult: (Unit?) -> Unit = {}) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/${userId}/cv/${fileName}")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                onResult(Unit)
            }.addOnFailureListener {
                onResult(Unit)
            }
    }


    /**
     * Récupère un CV d'un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     * @param fileName Le nom du fichier CV.
     * @param context Le contexte de l'application.
     * @param onResult Callback avec le résultat de l'opération.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun fetchCv(userId: String, fileName: String, context: Context, onResult: (Unit?) -> Unit = {}) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/${userId}/cv/${fileName}")
        val localFile = File(context.cacheDir, fileName)

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    localFile
                )

                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
                onResult(Unit)
            }.addOnFailureListener {
                onResult(Unit)
            }
    }
}