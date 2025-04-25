package com.project.stageconnect.ui.auth

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.stageconnect.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _signupState = MutableStateFlow<SignupResult>(SignupResult.Idle)
    val signupState: StateFlow<SignupResult> = _signupState

    fun signup(
        typeKey: String,
        email: String,
        password: String,
        firstname: String? = null,
        lastname: String? = null,
        name: String? = null,
        phone: String,
        address: String
    ) {
        _signupState.value = SignupResult.Idle
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val userData = mutableMapOf<String, Any>(
                        "type" to typeKey,
                        "email" to email,
                        "phone" to phone,
                        "address" to address
                    )

                    when (typeKey) {
                        "intern" -> {
                            userData["firstname"] = firstname ?: ""
                            userData["lastname"] = lastname ?: ""
                        }
                        "company", "educational" -> {
                            userData["structname"] = name ?: ""
                        }
                    }

                    db.collection("users")
                        .document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _signupState.value = SignupResult.Success
                        }
                        .addOnFailureListener {
                            _signupState.value = SignupResult.Error("Erreur: ${it.message}")
                        }
                }
                .addOnFailureListener {
                    _signupState.value = SignupResult.Error("Erreur: ${it.message}")
                }
        }
    }
}

sealed class SignupResult {
    data object Idle : SignupResult()
    data object Loading : SignupResult()
    data object Success : SignupResult()
    data class Error(val message: String) : SignupResult()
}