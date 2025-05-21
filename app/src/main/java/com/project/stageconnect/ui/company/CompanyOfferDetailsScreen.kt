package com.project.stageconnect.ui.company

import android.widget.Toast
import com.project.stageconnect.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Internship
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.ApplicationViewModel
import com.project.stageconnect.viewmodel.InternshipViewModel

@Composable
fun CompanyOfferDetailsScreen(navController: NavController, offerId: String?) {

    val internshipViewModel: InternshipViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()
    var offer by remember { mutableStateOf<Internship?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val internshipState by internshipViewModel.internshipState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        internshipViewModel.loadInternship({ internship ->
            offer = internship
        }, offerId ?: "")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = { navController.navigate("offers") }
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }

        offer?.let { internship ->
            Column(modifier = Modifier
                .fillMaxSize()
            ) {

                Text(
                    text = internship.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

                Text(
                    text = "${internship.companyName} | ${Utils.extractPostalCodeAndCity(internship.location)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Button(onClick = { showDialog = true }) {
                    Text(stringResource(R.string.delete))
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton (
                                onClick = {
                                    internshipViewModel.deleteInternship(internship.id)
                                    applicationViewModel.deleteInternshipApplications(internship.id)
                                    showDialog = false
                                },
                                enabled = internshipState != DataResult.Loading
                            ) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        },
                        title = {
                            Text(stringResource(R.string.internship_offer))
                        },
                        text = {
                            Text(stringResource(R.string.do_you_want_to_delete_that_offer))
                        }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = internship.description,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                    modifier = Modifier.padding(top = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    text = stringResource(R.string.location),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = internship.location,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                    modifier = Modifier.padding(top = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    text = stringResource(R.string.duration),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = internship.duration,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (internshipState is DataResult.Error) {
                Spacer(modifier = Modifier.height(12.dp))

                LaunchedEffect((internshipState as DataResult.Error).message) {
                    Toast.makeText(context, context.getString(R.string.error_message) + (internshipState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                }
            }

            if (internshipState == DataResult.Success) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.the_offer_has_been_deleted), Toast.LENGTH_SHORT).show()
                    navController.navigate("offers")
                }
            }
        }
    }
}