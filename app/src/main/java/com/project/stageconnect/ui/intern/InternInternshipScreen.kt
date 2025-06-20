package com.project.stageconnect.ui.intern

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.Offer
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.OfferViewModel

/**
 * Vue d'un stage qui est associé à un étudiant.
 *
 * @param currentUser L'utilisateur actuel.
 *
 * @return La vue d'un stage qui est associé à un étudiant.
 */
@Composable
fun InternInternshipScreen(currentUser: User) {
    val internshipViewModel: InternshipViewModel = viewModel()
    val offerViewModel: OfferViewModel = viewModel()
    val context = LocalContext.current

    var internship by remember { mutableStateOf<Internship?>(null) }
    var offer by remember { mutableStateOf<Offer?>(null) }

    LaunchedEffect(Unit) {
        internshipViewModel.loadInternshipByUser({ int ->
            internship = int
            offerViewModel.loadOffer({ off ->
                offer = off
            }, internship?.offerId ?: "")
        }, currentUser.uid)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        if (internship == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_internship_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
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

                    Row {
                        Button(
                            onClick = {
                                internshipViewModel.fetchAgreement({}, internship!!.id, internship!!.agreementName, context)
                            },
                        ) {
                            Text(text = stringResource(R.string.download_the_agreement))
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
}