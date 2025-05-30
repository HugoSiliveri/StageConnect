package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.Application
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable des données en lien avec les candidatures (`Application`).
 *
 * @property applicationRepository Repository des candidatures.
 * @property _applicationState Etat des candidatures.
 * @property applicationState Etat des candidatures sous forme de StateFlow.
 */
class ApplicationViewModel : ViewModel() {

    private val applicationRepository = ApplicationRepository()

    private val _applicationState = MutableStateFlow<DataResult>(DataResult.Idle)
    val applicationState: StateFlow<DataResult> = _applicationState

    /**
     * Charge les informations d'une candidature spécifique.
     *
     * @param onApplicationLoaded Callback avec la candidature chargée.
     * @param applicationId L'identifiant de la candidature.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadApplication(onApplicationLoaded: (Application?) -> Unit, applicationId: String) {
        applicationRepository.getApplication(applicationId) { application ->
            onApplicationLoaded(application)
        }
    }

    /**
     * Charge les informations d'une candidature spécifique par l'utilisateur et l'offre de stage.
     *
     * @param onApplicationLoaded Callback avec la candidature chargée.
     * @param userId L'identifiant de l'utilisateur.
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadApplicationByUserAndOffer(onApplicationLoaded: (Application?) -> Unit, userId: String, offerId: String) {
        applicationRepository.getApplicationByUserAndOffer(userId, offerId) { application ->
            onApplicationLoaded(application)
        }
    }

    /**
     * Charge les candidatures aux offres de stage d'une entreprise spécifique.
     *
     * @param onApplicationsLoaded Callback avec la liste des candidatures.
     * @param companyId L'identifiant de l'entreprise.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadCompanyApplications(onApplicationsLoaded: (List<Application>) -> Unit, companyId: String) {
        applicationRepository.getCompanyApplications(companyId) { applications ->
            onApplicationsLoaded(applications)
        }
    }

    /**
     * Crée une nouvelle candidature pour un utilisateur et une offre de stage spécifique.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la création a réussi ou non.
     */
    fun createApplication(userId: String, offerId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.createApplication(userId, offerId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Accepte une candidature spécifique.
     *
     * @param applicationId L'identifiant de la candidature.
     *
     * @return Un résultat indiquant si l'acceptation a réussi ou non.
     */
    fun acceptApplication(applicationId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.acceptApplication(applicationId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Refuse une candidature spécifique.
     *
     * @param applicationId L'identifiant de la candidature.
     *
     * @return Un résultat indiquant si le refus a réussi ou non.
     */
    fun denyApplication(applicationId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.denyApplication(applicationId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Annule une candidature spécifique.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si l'annulation a réussi ou non.
     */
    fun cancelApplication(userId: String, offerId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.cancelApplication(userId, offerId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Supprime toutes les candidatures liées à une offre de stage spécifique.
     *
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la suppression a réussi ou non.
     */
    fun deleteofferApplications(offerId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.deleteofferApplications(offerId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}