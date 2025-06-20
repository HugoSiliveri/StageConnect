package com.project.stageconnect.ui.intern

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
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
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue du compte d'un stagiaire.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 * @param onLogout La fonction à appeler lorsque l'utilisateur se déconnecte.
 *
 * @return La vue du compte d'un stagiaire.
 */
@Composable
fun InternAccountScreen(currentUser: User, navController: NavController, onLogout: () -> Unit) {

    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.userState.collectAsState()
    val context = LocalContext.current

    var institution by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()

        userViewModel.loadUser({ user ->
            institution = user
        }, currentUser.institutionId)
    }

    LazyColumn (
        modifier = Modifier
            .wrapContentHeight()
            .padding(8.dp)
            .fillMaxSize()
    ) {
        item {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row {
                        Text(
                            text = currentUser.firstname,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )

                        Text(
                            text = currentUser.lastname,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(start = 6.dp),
                        )
                    }
                }

                Row {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { navController.navigate("account_edition") }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "")
                    }

                    FilledTonalIconButton(
                        onClick = {
                            userViewModel.signOut()
                            onLogout()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "")
                    }
                }

                if (userState is DataResult.Error) {
                    LaunchedEffect((userState as DataResult.Error).message) {
                        Toast.makeText(context, context.getString(R.string.error_message) + (userState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                    }
                }

                if (userState == DataResult.Success) {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, context.getString(R.string.disconnection_successful) , Toast.LENGTH_SHORT).show()
                        navController.navigate("account")
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
                text = stringResource(R.string.email) + " : ${currentUser.email}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.phone) + " : ${currentUser.phone}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.address) + " : ${currentUser.address}",
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )

            Row ( verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.educational_institution) + " : ",
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
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


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

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
                        userViewModel.fetchCv({}, currentUser.uid, currentUser.cvName, context)
                    }
                ) {
                    Text(
                        currentUser.cvName,
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                        color = MaterialTheme.colorScheme.tertiary
                    )
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
                text = currentUser.description,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}