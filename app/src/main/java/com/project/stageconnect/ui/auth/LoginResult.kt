package com.project.stageconnect.ui.auth

open class LoginResult {
    data object Idle : LoginResult()
    data object Loading : LoginResult()
    data object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}