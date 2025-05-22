package com.project.stageconnect.ui.intern

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.UserViewModel

@Composable
fun InternAccountEditionScreen(currentUser: User, navController: NavController, onUpdated: () -> Unit) {

    val userViewModel: UserViewModel = viewModel()
    val userEditState by userViewModel.userState.collectAsState()
    val userUploadState by userViewModel.userState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf(currentUser.email) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var address by remember { mutableStateOf(currentUser.address) }
    var description by remember { mutableStateOf(currentUser.description) }
    var firstname by remember { mutableStateOf(currentUser.firstname) }
    var lastname by remember { mutableStateOf(currentUser.lastname) }
    var cvUri by remember { mutableStateOf<Uri?>(null) }
    var cvFileName by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                cvUri = uri
                cvFileName = Utils.getFileNameFromUri(context, uri)
            }
        }
    )

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
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text(stringResource(R.string.first_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text(stringResource(R.string.last_name)) },
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
                stringResource(R.string.cv),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(cvFileName, modifier = Modifier.padding(vertical = 4.dp))

            Row {
                TextButton(
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 4.dp)
                ) {
                    Text(stringResource(R.string.select_a_pdf_file))
                }

                Button(
                    enabled = cvUri != null && cvFileName.isNotEmpty(),
                    onClick = {
                        val uri = cvUri
                        val fileName = cvFileName
                        if (uri != null && fileName.isNotEmpty()) {
                            userViewModel.uploadCv({
                                Toast.makeText(context, context.getString(R.string.file_uploaded_successfully), Toast.LENGTH_SHORT).show()
                            },currentUser.uid, fileName, uri)
                        }
                    }
                ) {
                    Text(stringResource(R.string.upload))
                }
            }


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
                    onClick = { userViewModel.editUser(currentUser.uid, email, phone, address, firstname, lastname, currentUser.structname, description, currentUser.institutionId, cvFileName) },
                    enabled = userEditState != DataResult.Loading
                ) {
                    Text(stringResource(R.string.save_modifications))
                }
            }

            if (userEditState is DataResult.Error) {
                LaunchedEffect((userEditState as DataResult.Error).message) {
                    Toast.makeText(context, context.getString(R.string.error_message) + (userEditState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                }
            }

            if (userEditState == DataResult.Success) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.account_updated_successfully) , Toast.LENGTH_SHORT).show()
                    onUpdated()
                }
            }

            if (userUploadState is DataResult.Error) {
                LaunchedEffect((userUploadState as DataResult.Error).message) {
                    Toast.makeText(context, context.getString(R.string.error_message) + (userUploadState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                }
            }

            if (userUploadState == DataResult.Success) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.file_uploaded_successfully) , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
