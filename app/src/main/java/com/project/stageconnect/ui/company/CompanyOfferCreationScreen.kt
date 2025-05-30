package com.project.stageconnect.ui.company

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.OfferViewModel

/**
 * Vue de création d'une offre de stage.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue de création d'une offre de stage.
 */
@Composable
fun CompanyOfferCreationScreen(currentUser: User, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val offerViewModel: OfferViewModel = viewModel()
    val offerState by offerViewModel.offerState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = { navController.navigate("offers") }
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text(stringResource(R.string.duration)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { offerViewModel.createOffer(currentUser.uid, currentUser.structname, title, description, location, duration) },
                enabled = offerState != DataResult.Loading
            ) {
                Text(text = stringResource(R.string.create_offer))
            }

            if (offerState is DataResult.Error) {
                Spacer(modifier = Modifier.height(12.dp))

                LaunchedEffect((offerState as DataResult.Error).message) {
                    Toast.makeText(context, context.getString(R.string.error_message) + (offerState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                }
            }

            if (offerState == DataResult.Success) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.offer_created_successfully) , Toast.LENGTH_SHORT).show()
                    navController.navigate("offers")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}
