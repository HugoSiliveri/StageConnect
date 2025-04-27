package com.project.stageconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.stageconnect.ui.auth.LoginScreen
import com.project.stageconnect.ui.auth.LoginViewModel
import com.project.stageconnect.ui.auth.SignupScreen
import com.project.stageconnect.ui.auth.SignupViewModel
import com.project.stageconnect.ui.company.CompanyScreen
import com.project.stageconnect.model.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    val (startDestination, setStartDestination) = remember { mutableStateOf("home") }
    val isUserLoaded = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser { user ->
            if (user != null) {
                setStartDestination("home")
            } else {
                setStartDestination("login")
            }
            isUserLoaded.value = true
        }
    }

    if (!isUserLoaded.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("login") {
                LoginScreen(
                    viewModel = LoginViewModel(),
                    onLoginSuccess = {
                        userViewModel.loadCurrentUser {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onSignUpClick = { navController.navigate("signup") }
                )
            }
            composable("signup") {
                SignupScreen(
                    viewModel = SignupViewModel(),
                    onSignupSuccess = { navController.navigate("login") },
                    onLoginClick = { navController.navigate("login") }
                )
            }
            composable("home") {
                val currentUser = userViewModel.getCurrentUser()
                when (currentUser?.type) {
                    "intern" -> Text("Internview") // TODO: Remplacer par la vue pour les étudiants
                    "company" -> CompanyScreen()
                    "educational" -> Text("Educational view") // TODO: Remplacer par la vue pour les établissements
                }
            }
        }
    }
}
