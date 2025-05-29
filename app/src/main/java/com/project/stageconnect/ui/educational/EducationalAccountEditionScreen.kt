package com.project.stageconnect.ui.educational

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue d'édition de compte pour un établissement de formation.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 * @param onUpdated La fonction à appeler lorsque les informations sont mises à jour.
 *
 * @return La vue d'édition de compte pour un établissement de formation.
 */
@Composable
fun EducationalAccountEditionScreen(currentUser: User, navController: NavController, onUpdated: () -> Unit) {

    val userViewModel: UserViewModel = viewModel()
    val userState by userViewModel.userState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf(currentUser.email) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var address by remember { mutableStateOf(currentUser.address) }
    var description by remember { mutableStateOf(currentUser.description) }
    var structname by remember { mutableStateOf(currentUser.structname) }

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

            OutlinedTextField(
                value = structname,
                onValueChange = { structname = it },
                label = { Text(stringResource(R.string.name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = stringResource(R.string.informations),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )


            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { userViewModel.editUser(currentUser.uid, email, phone, address, currentUser.firstname, currentUser.lastname, structname, description, currentUser.institutionId, currentUser.cvName) },
                    enabled = userState != DataResult.Loading
                ) {
                    Text(stringResource(R.string.save_modifications))
                }
            }

            if (userState is DataResult.Error) {
                LaunchedEffect((userState as DataResult.Error).message) {
                    Toast.makeText(context, context.getString(R.string.error_message) + (userState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                }
            }

            if (userState == DataResult.Success) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.account_updated_successfully) , Toast.LENGTH_SHORT).show()
                    onUpdated()
                }
            }
        }
    }
}
