package com.project.stageconnect.model

/**
 * Modèle de données pour une conversation.
 *
 * @property id L'identifiant unique de la conversation.
 * @property userIds Les identifiants des utilisateurs participant à la conversation.
 * @property lastMessage Le dernier message envoyé dans la conversation.
 * @property timestamp L'heure à laquelle la conversation a été créée.
 */
data class Chat (
    var id: String = "",
    var userIds: List<String> = emptyList(),
    var lastMessage: String = "",
    var timestamp: Long = System.currentTimeMillis(),
)