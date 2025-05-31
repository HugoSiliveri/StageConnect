package com.project.stageconnect.model

/**
 * Modèle de données pour un message.
 *
 * @property senderId L'identifiant de l'utilisateur qui a envoyé le message.
 * @property content Le contenu du message.
 * @property timestamp L'heure à laquelle le message a été envoyé.
 */
data class Message (
    var senderId: String = "",
    var content: String = "",
    var timestamp: Long = System.currentTimeMillis(),
)