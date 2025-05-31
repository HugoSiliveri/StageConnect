package com.project.stageconnect.ui.company

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.InternshipViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue des détails d'un stage qui est associé à un étudiant.
 *
 * @param navController Le contrôleur de navigation.
 * @param internshipId L'identifiant du stage.
 *
 * @return La vue des détails d'un stage qui est associé à un étudiant.
 */
@Composable
fun CompanyInternshipDetailsScreen(navController: NavHostController, internshipId: String?) {
    val internshipViewModel: InternshipViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    var internship by remember { mutableStateOf<Internship?>(null) }
    var intern by remember { mutableStateOf<User?>(null) }
    var institution by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(internshipId) {
        internshipViewModel.loadInternship({ int ->
            internship = int

            val userId = int.userId
            userViewModel.loadUser({ usr ->
                intern = usr

                val institutionId = usr?.institutionId
                if (!institutionId.isNullOrEmpty()) {
                    userViewModel.loadUser({ inst ->
                        institution = inst
                    }, institutionId)
                }
            }, userId)
        }, internshipId ?: "")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        item {
            FilledTonalIconButton(onClick = { navController.navigate("interns") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row {
                        Text(
                            text = intern?.firstname.toString(),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )

                        Text(
                            text = intern?.lastname.toString(),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }

                FilledTonalIconButton(onClick = { navController.navigate("chat/${intern?.uid}") }) {
                    Icon(painterResource(R.drawable.chat), contentDescription = "")
                }
            }

            Button(
                onClick = {
                    internship?.let {
                        internshipViewModel.fetchAgreement({}, it.id, it.agreementName, context)
                    }
                },
            ) {
                Text(text = stringResource(R.string.download_the_agreement))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = stringResource(R.string.informations),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "${stringResource(R.string.email)} : ${intern?.email.orEmpty()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${stringResource(R.string.phone)} : ${intern?.phone.orEmpty()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "${stringResource(R.string.address)} : ${intern?.address.orEmpty()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.educational_institution) + " : ",
                    style = MaterialTheme.typography.bodyMedium
                )
                institution?.let { inst ->
                    TextButton(
                        onClick = { navController.navigate("institution_detail/${inst.uid}") },
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = inst.structname,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = stringResource(R.string.cv),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Image(painterResource(R.drawable.pdf), contentDescription = "pdf")
                TextButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        intern?.let {
                            userViewModel.fetchCv({}, it.uid, it.cvName, context)
                        }
                    }
                ) {
                    Text(
                        text = intern?.cvName.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
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
                text = intern?.description.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}