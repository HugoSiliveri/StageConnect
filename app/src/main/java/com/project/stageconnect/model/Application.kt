package com.project.stageconnect.model

/**
 * Modèle de données pour une candidature.
 *
 * @property id L'identifiant unique de la candidature.
 * @property userId L'identifiant de l'utilisateur ayant effectué la candidature.
 * @property offerId L'identifiant de l'offre de stage concernée.
 * @property status Le statut de la candidature (pending, accepted, denied).
 */
data class Application (
    var id: String = "",
    var userId: String = "",
    var offerId: String = "",
    var status: String = "",
)

