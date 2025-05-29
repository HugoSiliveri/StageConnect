package com.project.stageconnect.model

/**
 * Modèle de données pour un utilisateur.
 *
 * @property uid L'identifiant unique de l'utilisateur.
 * @property type Le type d'utilisateur (intern, company, educational).
 * @property email L'adresse e-mail de l'utilisateur.
 * @property phone Le numéro de téléphone de l'utilisateur.
 * @property address L'adresse de l'utilisateur.
 * @property firstname Le prénom de l'utilisateur (pour les étudiants).
 * @property lastname Le nom de famille de l'utilisateur (pour les étudiants).
 * @property structname Le nom de l'entreprise ou de l'établissement (pour les entreprises et établissements).
 * @property description La description de l'utilisateur.
 * @property institutionId L'identifiant de l'établissement de l'utilisateur (pour les étudiants).
 * @property cvName Le nom du fichier CV de l'utilisateur.
 */
data class User(
    var uid: String = "",
    var type: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var firstname: String = "",
    var lastname: String = "",
    var structname: String = "",
    var description: String = "",
    var institutionId: String = "",
    var cvName: String = ""
)