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
}