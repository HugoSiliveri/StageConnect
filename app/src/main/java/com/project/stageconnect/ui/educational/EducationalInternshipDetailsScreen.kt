package com.project.stageconnect.ui.educational

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.Offer
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.OfferViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue des détails d'un stage associé à un étudiant.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 * @param internshipId L'identifiant du stage.
 */
@Composable
fun EducationalInternshipDetailsScreen(currentUser: User, navController: NavController, internshipId: String?) {
    val offerViewModel: OfferViewModel = viewModel()
    val internshipViewModel: InternshipViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    var internship by remember { mutableStateOf<Internship?>(null) }
    var student by remember { mutableStateOf<User?>(null) }
    var offer by remember { mutableStateOf<Offer?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        internshipViewModel.loadInternship({ int ->
            internship = int

            userViewModel.loadUser({ user ->
                student = user
            }, internship?.userId ?: "")

            offerViewModel.loadOffer({ off ->
                offer = off
            }, internship?.offerId ?: "")
        }, internshipId ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = { navController.navigate("students") }
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }

        offer?.let { offer ->
            Column(modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

                Text(
                    text = "${offer.companyName} | ${Utils.extractPostalCodeAndCity(offer.location)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                if (internship?.status == "not_started") {
                    Row {
                        Button(
                            onClick = { // TODO : Faire les vues pour la convention de stage
                                navController.navigate("agreement/${internship?.id}/${internship?.step}") },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(R.string.check_the_agreement))
                        }
                    }
                }

                Row (
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (internship?.status) {
                        "not_started" -> {
                            Image(painterResource(R.drawable.schedule), contentDescription = "hourglass", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
                            Text(
                                text = stringResource(R.string.the_internship_agreement_of) + student?.firstname + " " + student?.lastname + stringResource(R.string.is_in_working_progress),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        "in_progress" -> {
                            Icon(Icons.Default.Check, contentDescription = "check", tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = student?.firstname + " " + student?.lastname + stringResource(R.string.is_on_internship),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        "finished" -> {
                            Icon(Icons.Default.Clear, contentDescription = "clear", tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = student?.firstname + " " + student?.lastname + stringResource(R.string.has_finished_his_her_internship),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        else -> {}
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = offer.description,
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
                    text = offer.location,
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
                    text = offer.duration,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}