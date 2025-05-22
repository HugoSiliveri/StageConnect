package com.project.stageconnect.ui.company

import android.view.animation.RotateAnimation
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.ApplicationViewModel
import com.project.stageconnect.viewmodel.UserViewModel

@Composable
fun CompanyApplicationDetailsScreen(navController: NavHostController, applicationId: String?) {

    val userViewModel: UserViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()
    val context = LocalContext.current

    var userInstitution by remember { mutableStateOf<User?>(null) }
    var application by remember { mutableStateOf<Application?>(null) }
    var user by remember { mutableStateOf<User?>(null) }

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

                        Text(
                            candidate.type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (application?.status == "pending"){
                    Row {
                        Button(onClick = { }, modifier = Modifier.padding(end = 8.dp)) {
                            Text(stringResource(R.string.accept))
                        }
                        Button(onClick = {  }) {
                            Text(stringResource(R.string.refuse))
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