package com.project.stageconnect.ui.auth

open class SignupResult {
    data object Idle : SignupResult()
    data object Loading : SignupResult()
    data object Success : SignupResult()
    data class Error(val message: String) : SignupResult()
}