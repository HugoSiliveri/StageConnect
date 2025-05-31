package com.project.stageconnect.ui.intern

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue des détails d'un établissement de formation.
 *
 * @param institutionId L'identifiant de l'établissement de formation.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue des détails d'un établissement de formation.
 */
@Composable
fun InternInstitutionDetailsScreen(institutionId: String?, navController: NavController) {
    val userViewModel: UserViewModel = viewModel()

    var institution by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadUser({ user ->
            institution = user
        }, institutionId.toString())
    }

    LazyColumn (
        modifier = Modifier
            .wrapContentHeight()
            .padding(8.dp)
            .fillMaxSize()
    ) {
        item {
            FilledTonalIconButton(
                onClick = { navController.navigate("account") }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = institution?.structname.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }

                Row {
                    FilledTonalIconButton(
                        onClick = { navController.navigate("chat/${institutionId}") }
                    ) {
                        Icon(painterResource(R.drawable.chat), contentDescription = "")
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = stringResource(R.string.informations),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "${stringResource(R.string.email)} : ${institution?.email}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "${stringResource(R.string.phone)} : ${institution?.phone}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "${stringResource(R.string.address)} : ${institution?.address}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = institution?.description.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}