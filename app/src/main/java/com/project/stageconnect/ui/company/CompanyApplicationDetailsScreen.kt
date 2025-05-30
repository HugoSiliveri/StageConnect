package com.project.stageconnect.ui.company

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.stageconnect.R
import com.project.stageconnect.model.Application
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Offer
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.MessagingService
import com.project.stageconnect.viewmodel.ApplicationViewModel
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.OfferViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue de détails d'une candidature.
 *
 * @param navController Le contrôleur de navigation.
 * @param applicationId L'identifiant de la candidature.
 *
 * @return La vue de détails d'une candidature.
 */
@Composable
fun CompanyApplicationDetailsScreen(navController: NavHostController, applicationId: String?) {

    val userViewModel: UserViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()
    val internshipViewModel: InternshipViewModel = viewModel()
    val offerViewModel: OfferViewModel = viewModel()
    val context = LocalContext.current
    val messagingService = MessagingService()

    var userInstitution by remember { mutableStateOf<User?>(null) }
    var application by remember { mutableStateOf<Application?>(null) }
    var offer by remember { mutableStateOf<Offer?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showDenyDialog by remember { mutableStateOf(false) }
    val applicationState by applicationViewModel.applicationState.collectAsState()

    LaunchedEffect(applicationId) {
        if (applicationId == null) return@LaunchedEffect

        applicationViewModel.loadApplication({ app ->
            if (app != null) {
                userViewModel.loadUser({ usr ->
                    user = usr
                    if (usr != null && usr.institutionId.isNotEmpty()) {
                        userViewModel.loadUser({ usrInst ->
                            userInstitution = usrInst
                        }, usr.institutionId)
                    }
                }, app.userId)

                offerViewModel.loadOffer({ off ->
                    offer = off
                }, app.offerId)
                application = app
            }
        }, applicationId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            FilledTonalIconButton(
                onClick = { navController.navigate("applications") }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            user?.let { candidate ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Row {
                            Text(
                                candidate.firstname,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            )
                            Text(
                                candidate.lastname,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }
                    }
                }

                if (application?.status == "pending"){
                    Row {
                        Button(onClick = { showAcceptDialog = true }, modifier = Modifier.padding(end = 8.dp)) {
                            Text(stringResource(R.string.accept))
                        }
                        Button(onClick = { showDenyDialog = true }) {
                            Text(stringResource(R.string.refuse))
                        }
                    }

                    if (showAcceptDialog) {
                        AlertDialog(
                            onDismissRequest = { showAcceptDialog = false },
                            confirmButton = {
                                TextButton (
                                    onClick = {
                                        applicationViewModel.acceptApplication(application?.id ?: "")
                                        internshipViewModel.createInternship(offer?.id.toString(), candidate.uid)
                                        showAcceptDialog = false
                                    },
                                    enabled = applicationState != DataResult.Loading
                                ) {
                                    Text(stringResource(R.string.ok))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAcceptDialog = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            },
                            title = {
                                Text(stringResource(R.string.application))
                            },
                            text = {
                                Text(stringResource(R.string.do_you_want_to_accept_this_application))
                            }
                        )
                    }

                    if (showDenyDialog) {
                        AlertDialog(
                            onDismissRequest = { showDenyDialog = false },
                            confirmButton = {
                                TextButton (
                                    onClick = {
                                        applicationViewModel.denyApplication(application?.id ?: "")
                                        showDenyDialog = false
                                    },
                                    enabled = applicationState != DataResult.Loading
                                ) {
                                    Text(stringResource(R.string.ok))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDenyDialog = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            },
                            title = {
                                Text(stringResource(R.string.application))
                            },
                            text = {
                                Text(stringResource(R.string.do_you_want_to_deny_this_application))
                            }
                        )
                    }

                    if (applicationState is DataResult.Error) {
                        Spacer(modifier = Modifier.height(12.dp))

                        LaunchedEffect((applicationState as DataResult.Error).message) {
                            Toast.makeText(context, context.getString(R.string.error_message) + (applicationState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (applicationState == DataResult.Success) {
                        LaunchedEffect(Unit) {
                            if (application != null) {
                                messagingService.sendNotificationToUser(candidate.uid, context.getString(R.string.application_accepted), context.getString(R.string.your_application_to_the_offer) + offer?.title  + context.getString(R.string.has_been_accepted))
                                Toast.makeText(context, context.getString(R.string.you_have_accepted_the_application), Toast.LENGTH_SHORT).show()

                            }
                            else {
                                messagingService.sendNotificationToUser(candidate.uid, context.getString(R.string.application_denied), context.getString(R.string.your_application_to_the_offer) + offer?.title  + context.getString(R.string.has_been_denied))
                                Toast.makeText(context, context.getString(R.string.you_have_denied_the_application), Toast.LENGTH_SHORT).show()
                            }
                            navController.navigate("offers")
                        }
                    }

                } else {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (application?.status) {
                            "accepted" -> {
                                Icon(Icons.Default.Check, contentDescription = "check", tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    stringResource(R.string.this_application_has_been_accepted),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            "denied" -> {
                                Icon(Icons.Default.Clear, contentDescription = "clear", tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    stringResource(R.string.this_application_has_been_denied),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = stringResource(R.string.informations),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "${stringResource(R.string.email)} : ${candidate.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "${stringResource(R.string.phone)} : ${candidate.phone}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "${stringResource(R.string.address)} : ${candidate.address}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "${stringResource(R.string.educational_institution)} : ${userInstitution?.structname ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = stringResource(R.string.cv),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Image(painterResource(R.drawable.pdf), "pdf")
                    TextButton (
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            userViewModel.fetchCv({}, candidate.uid, candidate.cvName, context)
                        }
                    ) {
                        Text(
                            candidate.cvName,
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = candidate.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}