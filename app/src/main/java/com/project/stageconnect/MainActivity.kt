package com.project.stageconnect

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.stageconnect.theme.StageConnectTheme
import com.project.stageconnect.ui.auth.LoginScreen
import com.project.stageconnect.ui.auth.SignupScreen
import com.project.stageconnect.ui.company.CompanyScreen
import com.project.stageconnect.ui.educational.EducationalScreen
import com.project.stageconnect.ui.intern.InternScreen
import com.project.stageconnect.viewmodel.LoginViewModel
import com.project.stageconnect.viewmodel.SignupViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Activité principale de l'application.
 *
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDarkTheme = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        enableEdgeToEdge(
            statusBarStyle = if (isDarkTheme) {
                SystemBarStyle.dark(Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            },
            navigationBarStyle = if (isDarkTheme) {
                SystemBarStyle.dark(Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            }
        )

        setContent {
            AppNavigation()
        }
    }
}

/**
 * Composant de navigation de l'application.
 *
 * @return Le composant de navigation.
 */
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    val (startDestination, setStartDestination) = remember { mutableStateOf("login") }
    val isUserLoaded = remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser { user ->
            setStartDestination(if (user != null) "home" else "login")
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
                                    onUpdated = {
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
                                    onUpdated = {
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
                                    onUpdated = {
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
