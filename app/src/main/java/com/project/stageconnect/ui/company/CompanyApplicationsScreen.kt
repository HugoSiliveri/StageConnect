package com.project.stageconnect.ui.company

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
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.stageconnect.R
import com.project.stageconnect.model.Application
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.ApplicationViewModel
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyApplicationsScreen(currentUser: User, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    val internshipViewModel: InternshipViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    var offers by remember { mutableStateOf<List<Internship>>(emptyList()) }
    var applications by remember { mutableStateOf<List<Application>>(emptyList()) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        applicationViewModel.loadCompanyApplications({ list ->
            applications = list
            val userIds = list.map { it.userId }.distinct()
            userViewModel.loadUsers({ list2 ->
                users = list2
            }, userIds)
        }, currentUser.uid)

        internshipViewModel.loadCompanyInternships({ list ->
            offers = list
        }, currentUser.uid)
    }

    val filteredTriples = applications.mapNotNull { app ->
        val offer = offers.find { it.id == app.internshipId }
        val user = users.find { it.uid == app.userId }
        if (offer != null && user != null &&
            (searchQuery.isBlank()
                    || offer.title.contains(searchQuery, ignoreCase = true)
                    || user.firstname.contains(searchQuery, ignoreCase = true)
                    || user.lastname.contains(searchQuery, ignoreCase = true)
                    || app.status.contains(searchQuery, ignoreCase = true)
            )
        ) {
            Triple(user, offer, app)
        } else null
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { isActive = false },
            active = false,
            onActiveChange = { isActive = it },
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTriples.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.no_candidate_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(filteredTriples) { index, (user, internship, application) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("application_details/${application.id}")
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
                                    "${user.firstname} ${user.lastname}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = when (application.status) {
                                            "pending" -> context.getString(R.string.pending)
                                            "accepted" -> context.getString(R.string.accepted)
                                            "denied" -> context.getString(R.string.denied)
                                            else -> application.status
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (application.status) {
                                            "pending" -> MaterialTheme.colorScheme.secondary
                                            "accepted" -> MaterialTheme.colorScheme.primary
                                            "denied" -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.weight(0.3f)
                                    )
                                    Text(
                                        text = internship.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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