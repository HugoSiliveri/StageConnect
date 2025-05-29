package com.project.stageconnect.model

/**
 * Modèle de données pour le résultat d'une opération sur les données.
 *
 * @property Idle L'état initial.
 * @property Loading L'état pendant la récupération des données.
 * @property Success L'état de succès après la récupération des données.
 * @property Error L'état d'erreur avec un message d'erreur.
 */
open class DataResult {
    data object Idle : DataResult()
    data object Loading : DataResult()
    data object Success : DataResult()
    data class Error(val message: String) : DataResult()
}