package com.project.stageconnect.ui.intern

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.MessagingService
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.InternshipViewModel

/**
 * Vue du processus de création de la convention du stage.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 * @param internshipId L'identifiant du stage.
 *
 * @return La vue du processus de création de la convention du stage.
 */
@Composable
fun InternAgreementScreen(currentUser: User, navController: NavHostController, internshipId: String?) {
    val internshipViewModel: InternshipViewModel = viewModel()
    val messagingService = MessagingService()
    val context = LocalContext.current
    val uploaded = remember { mutableStateOf(false) }

    var internship by remember { mutableStateOf<Internship?>(null) }
    var agreementUri by remember { mutableStateOf<Uri?>(null) }
    var agreementFileName by remember { mutableStateOf("") }
    var showUploadToast by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                agreementUri = uri
                agreementFileName = Utils.getFileNameFromUri(context, uri)

                internshipViewModel.uploadAgreement({
                    showUploadToast = true
                    uploaded.value = true


                }, internshipId ?: return@rememberLauncherForActivityResult, agreementFileName, uri)
            }
        }
    )

    LaunchedEffect(Unit) {
        internshipViewModel.loadInternship({ int ->
            internship = int
        }, internshipId ?: "")
    }

    LaunchedEffect(showUploadToast) {
        if (showUploadToast) {
            Toast.makeText(context, context.getString(R.string.file_uploaded_successfully), Toast.LENGTH_SHORT).show()
            showUploadToast = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = { navController.navigate("offer_details/${internship?.offerId}") }
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }

        internship?.let { internship ->

            LinearProgressIndicator(
                progress = { (internship.step + 1) / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )

            Column {
                when (internship.step) {
                    0 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row (
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.step_1),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = stringResource(R.string.step1_text_intern),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )

                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        launcher.launch("application/pdf")
                                    }
                                ) {
                                    Text(stringResource(R.string.upload))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button (
                                onClick = {
                                    messagingService.sendNotificationToUser(currentUser.institutionId, context.getString(R.string.internship_agreement), currentUser.firstname + " " + currentUser.lastname + context.getString(R.string.has_submitted_an_internship_agreement))
                                    internshipViewModel.setStep(internship.id, 1)
                                    internshipViewModel.setAgreementName(internship.id, agreementFileName)
                                    navController.navigate("agreement/$internshipId")
                                },
                                enabled = uploaded.value
                            ){
                                Text(text = stringResource(R.string.next))
                            }
                        }
                    }
                    1 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row (
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.step_2),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = stringResource(R.string.step_2_text_intern),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                    2 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row (
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.step_3),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = stringResource(R.string.step_3_text_intern),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )

                            val bullet = "\u2022"
                            val messages = listOf(
                                stringResource(R.string.step_3_text_intern_1),
                                stringResource(R.string.step_3_text_intern_2),
                                stringResource(R.string.step_3_text_intern_3),
                                stringResource(R.string.step_3_text_intern_4)
                            )

                            val paragraphStyle = ParagraphStyle()
                            val bodyMedium = MaterialTheme.typography.bodyMedium

                            Text(
                                text = buildAnnotatedString {
                                    messages.forEach { message ->
                                        withStyle(style = paragraphStyle) {
                                            withStyle(style = SpanStyle(fontSize = bodyMedium.fontSize)) {
                                                append(bullet)
                                                append("\t\t")
                                                append(message)
                                            }
                                        }
                                    }
                                },
                                style = bodyMedium
                            )


                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        launcher.launch("application/pdf")
                                    }
                                ) {
                                    Text(stringResource(R.string.upload))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button (
                                onClick = {
                                    messagingService.sendNotificationToUser(currentUser.institutionId, context.getString(R.string.internship_agreement), currentUser.firstname + " " + currentUser.lastname + context.getString(R.string.has_submitted_an_internship_agreement))
                                    internshipViewModel.setStep(internship.id, 3)
                                    internshipViewModel.setAgreementName(internship.id, agreementFileName)
                                    navController.navigate("agreement/$internshipId")
                                },
                                enabled = uploaded.value
                            ){
                                Text(text = stringResource(R.string.next))
                            }
                        }
                    }
                    3 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row(
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.step_4),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = stringResource(R.string.step_4_text_intern),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                }
            }
        }
    }
}