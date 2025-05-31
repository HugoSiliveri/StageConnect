package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Offer
import com.project.stageconnect.model.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable des données en lien avec les offres de stage (`offer`).
 *
 * @property offerRepository Repository des offres de stage.
 * @property _offerState Etat des offres de stage.
 * @property offerState Etat des offres de stage sous forme de StateFlow.
 */
class OfferViewModel : ViewModel() {

    private val offerRepository = OfferRepository()

    private val _offerState = MutableStateFlow<DataResult>(DataResult.Idle)
    val offerState: StateFlow<DataResult> = _offerState

    /**
     * Charge les informations d'une offre de stage spécifique.
     *
     * @param onOffersLoaded Callback avec l'offre de stage chargée.
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadOffer(onOffersLoaded: (Offer?) -> Unit, offerId: String) {
        offerRepository.getOffer(offerId) { offer ->
            onOffersLoaded(offer)
        }
    }

    /**
     * Charge les offres de stage disponibles.
     *
     * @param onOffersLoaded Callback avec la liste des offres de stage.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadNoApplicationAndNoInternshipOffers(onOffersLoaded: (List<Offer>) -> Unit, userId: String) {
        offerRepository.getNoApplicationAndNoInternshipOffers(userId) { list ->
            onOffersLoaded(list)
        }
    }

    /**
     * Charge les offres de stage d'une entreprise spécifique qui n'ont pas encore de stage.
     *
     * @param onOffersLoaded Callback avec la liste des offres de stage.
     * @param companyId L'identifiant de l'entreprise.
     */
    fun loadNoInternshipCompanyOffers(onOffersLoaded: (List<Offer>) -> Unit, companyId: String) {
        offerRepository.getNoInternshipCompanyOffers(companyId) { list ->
            onOffersLoaded(list)
        }
    }

    /**
     * Charge les offres de stage auxquelles l'utilisateur est déjà inscris.
     *
     * @param onOffersLoaded Callback avec la liste des offres de stage.
     * @param userId L'identifiant de l'utilisateur.
     *
     * @return Un résultat indiquant si la récupération a réussi ou non.
     */
    fun loadApplicationOffers(onOffersLoaded: (List<Offer>) -> Unit, userId: String) {
        offerRepository.getApplicationOffers(userId) { list ->
            onOffersLoaded(list)
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
    fun createOffer(
        companyId: String,
        companyName: String,
        title: String,
        description: String,
        location: String,
        duration: String
    ) {
        viewModelScope.launch {
            _offerState.value = DataResult.Loading
            val result = offerRepository.createOffer(
                companyId,
                companyName,
                title,
                description,
                location,
                duration
            )
            _offerState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    /**
     * Supprime une offre de stage spécifique.
     *
     * @param offerId L'identifiant de l'offre de stage.
     *
     * @return Un résultat indiquant si la suppression a réussi ou non.
     */
    fun deleteOffer(offerId: String){
        viewModelScope.launch {
            _offerState.value = DataResult.Loading
            val result = offerRepository.deleteOffer(offerId)
            _offerState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}