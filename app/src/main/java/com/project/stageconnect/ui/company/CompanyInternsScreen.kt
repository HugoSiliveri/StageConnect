package com.project.stageconnect.ui.company

import android.util.Log
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
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.UserViewModel
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Vue des stagiaires d'une entreprise.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue des stagiaires d'une entreprise.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInternsScreen(currentUser: User, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val userViewModel: UserViewModel = viewModel()
    val internshipViewModel: InternshipViewModel = viewModel()

    var interns by remember { mutableStateOf<List<User>>(emptyList()) }
    var internships by remember { mutableStateOf<List<Internship>>(emptyList()) }
    var internsInternshipMap by remember { mutableStateOf<Map<User, Internship?>>(emptyMap()) }

    LaunchedEffect(Unit) {
        userViewModel.loadInterns({ list ->
            interns = list
            val internIds = list.map { it.uid }.distinct()
            internshipViewModel.loadUsersInternships({ internshipList ->
                internships = internshipList

                val map = list.associateWith { intern ->
                    internships.find { it.userId == intern.uid }
                }

                internsInternshipMap = map
            }, internIds)
        }, currentUser.uid)
    }

    val filteredInternsInternships = internsInternshipMap.filter { mapEntry ->
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

        if (filteredInternsInternships.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_interns_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(filteredInternsInternships.entries.toList()) { index, (intern, internship) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (internship != null) {
                                    navController.navigate("internship_details/${internship.id}")
                                }
                            }
                            .padding(vertical = 2.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column (modifier = Modifier.weight(1f)) {
                                Text(
                                    "${intern.firstname} ${intern.lastname}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { navController.navigate("chat/${intern.uid}") }
                            ) {
                                Icon(painterResource(R.drawable.chat), contentDescription = "")
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp),)
                    }
                }
            }
        }
    }
}