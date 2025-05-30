package com.project.stageconnect.ui.educational

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue qui affiche la liste des étudiants de l'établissement.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue qui affiche la liste des étudiants de l'établissement.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalStudentsScreen(currentUser: User, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val userViewModel: UserViewModel = viewModel()
    val internshipViewModel: InternshipViewModel = viewModel()
    val context = LocalContext.current

    var students by remember { mutableStateOf<List<User>>(emptyList()) }
    var internships by remember { mutableStateOf<List<Internship>>(emptyList()) }
    var studentInternshipMap by remember { mutableStateOf<Map<User, Internship?>>(emptyMap()) }

    LaunchedEffect(Unit) {
        userViewModel.loadStudents({ list ->
            students = list
            val studentIds = list.map { it.uid }.distinct()
            internshipViewModel.loadUsersInternships({ internshipList ->
                internships = internshipList

                val map = list.associateWith { student ->
                    internships.find { it.userId == student.uid }
                }

                studentInternshipMap = map
            }, studentIds)
        }, currentUser.uid)
    }

    val filteredStudentsInternships = studentInternshipMap.filter { mapEntry ->
        searchQuery.isBlank() || listOf(
            mapEntry.key.firstname,
            mapEntry.key.lastname,
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { isActive = false },
            active = false,
            onActiveChange = { isActive = it },
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredStudentsInternships.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_students_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(filteredStudentsInternships.entries.toList()) { index, (student, internship) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (internship != null) {
                                    navController.navigate("internship_details/${internship.id}")
                                }
                            }
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "${student.firstname} ${student.lastname}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = when (internship?.status) {
                                            "not_started" -> context.getString(R.string.awaiting_administrative_validation)
                                            "in_progress" -> context.getString(R.string.in_training)
                                            "finished" -> context.getString(R.string.finished)
                                            else -> context.getString(R.string.looking_for_an_internship)
                                        }.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (internship?.status) {
                                            "pending" -> MaterialTheme.colorScheme.secondary
                                            "accepted" -> MaterialTheme.colorScheme.primary
                                            "denied" -> MaterialTheme.colorScheme.error
                                            else -> Color.Black
                                        },
                                        modifier = Modifier.weight(0.3f)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp),)
                    }
                }
            }
        }
    }
}