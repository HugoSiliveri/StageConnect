package com.project.stageconnect

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.project.stageconnect.theme.StageConnectTheme
import com.project.stageconnect.ui.auth.LoginScreen
import com.project.stageconnect.viewmodel.LoginViewModel
import com.project.stageconnect.ui.auth.SignupScreen
import com.project.stageconnect.viewmodel.SignupViewModel
import com.project.stageconnect.ui.company.CompanyScreen
import com.project.stageconnect.ui.educational.EducationalScreen
import com.project.stageconnect.ui.intern.InternScreen
import com.project.stageconnect.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    val (startDestination, setStartDestination) = remember { mutableStateOf("login") }
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
                val currentUser = userViewModel.currentUser
                when (currentUser?.type) {
                    "intern" -> {
                        val internNavController = rememberNavController()
                        StageConnectTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                InternScreen (
                                    currentUser = currentUser,
                                    navController = internNavController,
                                    onLogout = {
                                        navController.navigate("login") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                    OnUpdated = {
                                        userViewModel.loadCurrentUser {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    "company" -> {
                        val companyNavController = rememberNavController()
                        StageConnectTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                CompanyScreen(
                                    currentUser = currentUser,
                                    navController = companyNavController,
                                    onLogout = {
                                        navController.navigate("login") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                    OnUpdated = {
                                        userViewModel.loadCurrentUser {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                    }
                    "educational" -> {
                        val educationalNavController = rememberNavController()
                        StageConnectTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                EducationalScreen(
                                    currentUser = currentUser,
                                    navController = educationalNavController,
                                    onLogout = {
                                        navController.navigate("login") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                    OnUpdated = {
                                        userViewModel.loadCurrentUser {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
