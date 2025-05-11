package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.repository.InternshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InternshipViewModel : ViewModel() {

    private val internshipRepository = InternshipRepository()

    private val _internshipState = MutableStateFlow<DataResult>(DataResult.Idle)
    val internshipState: StateFlow<DataResult> = _internshipState

    fun loadInternships(onInternshipsLoaded: (List<Internship>) -> Unit) {
        internshipRepository.getInternships { list ->
            onInternshipsLoaded(list)
        }
    }

    fun loadCompanyInternships(onInternshipsLoaded: (List<Internship>) -> Unit, companyId: String) {
        internshipRepository.getCompanyInternships(companyId) { list ->
            onInternshipsLoaded(list)
        }
    }

    fun loadInternship(onInternshipLoaded: (Internship?) -> Unit, internshipId: String) {
        internshipRepository.getInternship(internshipId) { internship ->
            onInternshipLoaded(internship)
        }
    }

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
}