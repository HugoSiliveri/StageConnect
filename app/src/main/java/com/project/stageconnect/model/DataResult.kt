package com.project.stageconnect.model

open class DataResult {
    data object Idle : DataResult()
    data object Loading : DataResult()
    data object Success : DataResult()
    data class Error(val message: String) : DataResult()
}