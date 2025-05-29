package com.project.stageconnect.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Repository responsable de l'authentification dans Firebase.
 *
 * @property auth Instance de FirebaseAuth.
 * @property db Instance de FirebaseFirestore.
 */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Effectue la connexion de l'utilisateur.
     *
     * @param email L'adresse e-mail de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     *
     * @return Un résultat indiquant si la connexion a réussi ou non.
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            updateUserToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Effectue la déconnexion de l'utilisateur.
     *
     * @param typeKey La clé du type d'utilisateur ("intern", "company", "educational").
     * @param email L'adresse e-mail de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @param firstname Le prénom de l'utilisateur (pour les étudiants).
     * @param lastname Le nom de famille de l'utilisateur (pour les étudiants).
     * @param name Le nom de l'entreprise ou de l'établissement (pour les entreprises et établissements).
     * @param phone Le numéro de téléphone de l'utilisateur.
     * @param address L'adresse de l'utilisateur.
     * @param institutionId L'identifiant de l'établissement de l'utilisateur (pour les étudiants).
     *
     * @return Un résultat indiquant si la déconnexion a réussi ou non.
     */
    suspend fun signupUser(
        typeKey: String,
        email: String,
        password: String,
        firstname: String? = null,
        lastname: String? = null,
        name: String? = null,
        phone: String,
        address: String,
        institutionId: String? = null
    ): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("UID introuvable"))

            val userData = mutableMapOf<String, Any>(
                "type" to typeKey,
                "email" to email,
                "phone" to phone,
                "address" to address,
            )

            when (typeKey) {
                "intern" -> {
                    userData["firstname"] = firstname ?: ""
                    userData["lastname"] = lastname ?: ""
                    userData["institutionId"] = institutionId ?: ""
                }
                "company", "educational" -> {
                    userData["structname"] = name ?: ""
                }
            }

            db.collection("users").document(uid).set(userData).await()
            updateUserToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Met à jour le token FCM de l'utilisateur.
     *
     * @return Un résultat indiquant si la mise à jour a réussi ou non.
     */
    suspend fun updateUserToken(): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("UID introuvable"))
            FirebaseMessaging.getInstance().deleteToken().await()

            val newToken = FirebaseMessaging.getInstance().token.await()

            db.collection("users").document(uid)
                .update("fcmToken", newToken)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}