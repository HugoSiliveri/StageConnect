package com.project.stageconnect.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.repository.InternshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable des données en lien avec les stages (`Internship`).
 *
 * @property internshipRepository Repository des stages.
 * @property _internshipState Etat des stages.
 * @property internshipState Etat des stages sous forme de StateFlow.
 */
class InternshipViewModel : ViewModel() {

    private val internshipRepository = InternshipRepository()
    private val _internshipState = MutableStateFlow<DataResult>(DataResult.Idle)
    val internshipState: StateFlow<DataResult> = _internshipState

    /**
     * Charge les détails d'un stage spécifique.
     *
     * @param onInternshipLoaded Callback avec le stage chargé.
     * @param internshipId L'identifiant du stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadInternship(onInternshipLoaded: (Internship) -> Unit, internshipId: String) {
        internshipRepository.getInternship(internshipId) { internship ->
            onInternshipLoaded(internship)
        }
    }

    /**
     * Charge les stages associés aux étudiants.
     *
     * @param onInternshipsLoaded Callback avec la liste des stages.
     * @param userIds Liste des identifiants des étudiants.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadUsersInternships(onInternshipsLoaded: (List<Internship>) -> Unit = {}, userIds: List<String>) {
        internshipRepository.getUsersInternships(userIds) { list ->
            onInternshipsLoaded(list)
        }
    }

    /**
     * Charge les détails du dernier stage pour un utilisateur spécifique.
     *
     * @param onInternshipLoaded Callback avec le stage chargé.
     * @param userId L'identifiant de l'utilisateur.
     */
    fun loadInternshipByUser(onInternshipLoaded: (Internship) -> Unit, userId: String) {
        internshipRepository.getInternshipByUser(userId) { internship ->
            onInternshipLoaded(internship)
        }
    }

    /**
     * Charge les détails d'un stage pour un utilisateur et une offre de stage spécifique.
     *
     * @param onInternshipLoaded Callback avec le stage chargé.
     * @param userId L'identifiant de l'utilisateur.
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadInternshipByUserAndOffer(onInternshipLoaded: (Internship) -> Unit, userId: String, offerId: String) {
        internshipRepository.getInternshipByUserAndOffer(userId, offerId) { internship ->
            onInternshipLoaded(internship)
        }
    }

    /**
     * Crée un nouveau stage pour un utilisateur et une offre de stage spécifique.
     *
     * @param offerId L'identifiant de l'offre de stage.
     * @param userId L'identifiant de l'utilisateur.
     *
     * @return Un résultat indiquant si la création a réussi ou non.
     */
    fun createInternship(offerId: String, userId: String) {
        viewModelScope.launch {
            _internshipState.value = DataResult.Loading
            val result = internshipRepository.createInternship(offerId, userId)
            _internshipState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Modifie l'étape du processus de création de la convention de stage
     *
     * @param internshipId L'identifiant du stage.
     * @param step L'étape du processus.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setStep(internshipId: String, step: Int) {
        internshipRepository.setStep(internshipId, step)
    }

    /**
     * Modifie le nom du fichier de la convention d'un stage.
     *
     * @param internshipId L'identifiant du stage.
     * @param name Le nom du fichier de la convention.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setAgreementName(internshipId: String, name: String) {
        internshipRepository.setAgreementName(internshipId, name)
    }

    /**
     * Modifie le statut d'un stage.
     *
     * @param internshipId L'identifiant du stage.
     * @param status Le statut du stage.
     *
     * @return Un résultat indiquant si la modification a réussi ou non.
     */
    fun setStatus(internshipId: String, status: String) {
        internshipRepository.setStatus(internshipId, status)
    }

    /**
     * Upload la convention d'un stage dans Firebase Storage.
     *
     * @param onResult Callback avec le résultat de l'opération.
     * @param internshipId L'identifiant du stage.
     * @param fileName Le nom du fichier de la convention.
     * @param fileUri L'URI du fichier de la convention.
     *
     * @return Un résultat indiquant si l'envoi a réussi ou non.
     */
    fun uploadAgreement(onResult: (Unit?) -> Unit = {}, internshipId: String, fileName: String, fileUri: Uri) {
        internshipRepository.uploadAgreement(internshipId, fileName, fileUri) { list ->
            onResult(list)
        }
    }

    /**
     * Récupère la convention d'un stage depuis Firebase Storage.
     *
     * @param onResult Callback avec le résultat de l'opération.
     * @param internshipId L'identifiant du stage.
     * @param fileName Le nom du fichier de la convention.
     * @param context Le contexte de l'application.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun fetchAgreement(onResult: (Unit?) -> Unit = {}, internshipId: String, fileName: String, context: Context) {
        internshipRepository.fetchAgreement(internshipId, fileName, context) { list ->
            onResult(list)
        }
    }

}