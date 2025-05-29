package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.repository.InternshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable des données en lien avec les offres de stage (`Internship`).
 *
 * @property internshipRepository Repository des offres de stage.
 * @property _internshipState Etat des offres de stage.
 * @property internshipState Etat des offres de stage sous forme de StateFlow.
 */
class InternshipViewModel : ViewModel() {

    private val internshipRepository = InternshipRepository()

    private val _internshipState = MutableStateFlow<DataResult>(DataResult.Idle)
    val internshipState: StateFlow<DataResult> = _internshipState

    /**
     * Charge les informations d'une offre de stage spécifique.
     *
     * @param onInternshipLoaded Callback avec l'offre de stage chargée.
     * @param internshipId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadInternship(onInternshipLoaded: (Internship?) -> Unit, internshipId: String) {
        internshipRepository.getInternship(internshipId) { internship ->
            onInternshipLoaded(internship)
        }
    }

    /**
     * Charge les offres de stage disponibles.
     *
     * @param onInternshipsLoaded Callback avec la liste des offres de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadNoApplicationInternships(onInternshipsLoaded: (List<Internship>) -> Unit, userId: String) {
        internshipRepository.getNoApplicationInternships(userId) { list ->
            onInternshipsLoaded(list)
        }
    }

    /**
     * Charge les offres de stage d'une entreprise spécifique.
     *
     * @param onInternshipsLoaded Callback avec la liste des offres de stage.
     * @param companyId L'identifiant de l'entreprise.
     */
    fun loadCompanyInternships(onInternshipsLoaded: (List<Internship>) -> Unit, companyId: String) {
        internshipRepository.getCompanyInternships(companyId) { list ->
            onInternshipsLoaded(list)
        }
    }

    /**
     * Charge les offres de stage auxquelles l'utilisateur est déjà inscris.
     *
     * @param onInternshipsLoaded Callback avec la liste des offres de stage.
     * @param userId L'identifiant de l'utilisateur.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadApplicationInternships(onInternshipsLoaded: (List<Internship>) -> Unit, userId: String) {
        internshipRepository.getApplicationInternships(userId) { list ->
            onInternshipsLoaded(list)
        }
    }

    /**
     * Crée une nouvelle offre de stage pour une entreprise spécifique.
     *
     * @param companyId L'identifiant de l'entreprise.
     * @param companyName Le nom de l'entreprise.
     * @param title Le titre de l'offre de stage.
     * @param description La description de l'offre de stage.
     * @param location La localisation de l'offre de stage.
     * @param duration La durée de l'offre de stage.
     *
     * @return Un résultat indiquant si la création a réussi ou non.
     */
    fun createInternship(
        companyId: String,
        companyName: String,
        title: String,
        description: String,
        location: String,
        duration: String
    ) {
        viewModelScope.launch {
            _internshipState.value = DataResult.Loading
            val result = internshipRepository.createInternship(
                companyId,
                companyName,
                title,
                description,
                location,
                duration
            )
            _internshipState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Supprime une offre de stage spécifique.
     *
     * @param internshipId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la suppression a réussi ou non.
     */
    fun deleteInternship(internshipId: String){
        viewModelScope.launch {
            _internshipState.value = DataResult.Loading
            val result = internshipRepository.deleteInternship(internshipId)
            _internshipState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}