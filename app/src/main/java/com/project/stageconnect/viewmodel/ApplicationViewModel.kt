package com.project.stageconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.repository.ApplicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApplicationViewModel : ViewModel() {

    private val applicationRepository = ApplicationRepository()

    private val _applicationState = MutableStateFlow<DataResult>(DataResult.Idle)
    val applicationState: StateFlow<DataResult> = _applicationState

    fun createCandidature(userId: String, internshipId: String, ) {
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
}