package com.project.stageconnect.model

/**
 * Modèle de données pour un stage.
 *
 * @property id L'identifiant unique du stage.
 * @property offerId L'identifiant de l'offre de stage concernée.
 * @property userId L'identifiant de l'utilisateur ayant effectué le stage.
 * @property status Le statut du stage (not_started, in_progress, finished).
 * @property step Progression dans le processus de création de la convention du stage (utilisé quand status = not_started).
 * @property agreementName Nom du fichier de la convention du stage.
 */
data class Internship (
    var id: String = "",
    var offerId: String = "",
    var userId: String = "",
    var status: String = "",
    var step: Int = 0,
    var agreementName: String = "",
)