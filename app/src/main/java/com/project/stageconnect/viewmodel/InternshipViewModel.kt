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

    private val _internships = MutableStateFlow<List<Internship>>(emptyList())
    val internships: StateFlow<List<Internship>> = _internships

    private val _internshipState = MutableStateFlow<DataResult>(DataResult.Idle)
    val internshipState: StateFlow<DataResult> = _internshipState

    fun loadInternships() {
        internshipRepository.getInternships { list ->
            _internships.value = list
        }
    }

    fun loadCompanyInternships(companyId: String) {
        internshipRepository.getCompanyInternships(companyId) { list ->
            _internships.value = list
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