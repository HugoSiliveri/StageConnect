package com.project.stageconnect.ui.educational

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.project.stageconnect.model.User

@Composable
fun EducationalStudentsScreen(currentUser: User, navController: NavController) {
    Text("Bienvenue sur la page des étudiants")
}