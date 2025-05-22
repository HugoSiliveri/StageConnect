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

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

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

    fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun uploadCv(userId: String, fileName: String, fileUri: Uri, onResult: (Unit?) -> Unit = {}) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/${userId}/cv/${fileName}")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                onResult(Unit)
            }.addOnFailureListener {
                onResult(Unit)
            }
    }

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