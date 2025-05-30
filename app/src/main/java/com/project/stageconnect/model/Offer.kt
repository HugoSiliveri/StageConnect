package com.project.stageconnect.model

/**
 * Modèle de données pour une offre de stage.
 *
 * @property id L'identifiant unique de l'offre de stage.
 * @property companyId L'identifiant de l'entreprise ayant créé l'offre.
 * @property companyName Le nom de l'entreprise ayant créé l'offre.
 * @property title Le titre de l'offre de stage.
 * @property description La description de l'offre de stage.
 * @property location La localisation du stage.
 * @property duration La durée du stage.
 */
data class Offer(
    var id: String = "",
    var companyId: String = "",
    var companyName: String = "",
    var title: String = "",
    var description: String = "",
    var location: String = "",
    var duration: String = ""
)