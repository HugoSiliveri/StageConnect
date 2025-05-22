package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.Application
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApplicationViewModel : ViewModel() {

    private val applicationRepository = ApplicationRepository()

    private val _applicationState = MutableStateFlow<DataResult>(DataResult.Idle)
    val applicationState: StateFlow<DataResult> = _applicationState

    fun loadApplication(onApplicationLoaded: (Application?) -> Unit, applicationId: String) {
        applicationRepository.getApplication(applicationId) { application ->
            onApplicationLoaded(application)
        }
    }

    fun loadApplicationByUserAndInternship(onApplicationLoaded: (Application?) -> Unit, userId: String, internshipId: String) {
        applicationRepository.getApplicationByUserAndInternship(userId, internshipId) { application ->
            onApplicationLoaded(application)
        }
    }

    fun loadCompanyApplications(onApplicationsLoaded: (List<Application>) -> Unit, companyId: String) {
        applicationRepository.getCompanyApplications(companyId) { applications ->
            onApplicationsLoaded(applications)
        }
    }

    fun createApplication(userId: String, internshipId: String, ) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.createApplication(userId, internshipId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    fun cancelApplication(userId: String, internshipId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.cancelApplication(userId, internshipId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }

    fun deleteInternshipApplications(internshipId: String) {
        viewModelScope.launch {
            _applicationState.value = DataResult.Loading
            val result = applicationRepository.deleteInternshipApplications(internshipId)
            _applicationState.value = if (result.isSuccess) {
                DataResult.Success
            } else {
                DataResult.Error(result.exceptionOrNull()?.message ?: "Erreur inconnue")
            }
        }
    }
}