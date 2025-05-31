package com.project.stageconnect.ui.educational

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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.Internship
import com.project.stageconnect.model.User
import com.project.stageconnect.utils.MessagingService
import com.project.stageconnect.utils.Utils
import com.project.stageconnect.viewmodel.ApplicationViewModel
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
fun EducationalAgreementScreen(currentUser: User, navController: NavController, internshipId: String?) {
    val internshipViewModel: InternshipViewModel = viewModel()
    val applicationViewModel: ApplicationViewModel = viewModel()
    val messagingService = MessagingService()

    val radioOptions = listOf(R.string.accept, R.string.refuse)
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    var internship by remember { mutableStateOf<Internship?>(null) }
    var agreementUri by remember { mutableStateOf<Uri?>(null) }
    var agreementFileName by remember { mutableStateOf("") }
    var showUploadToast by remember { mutableStateOf(false) }
    val uploaded = remember { mutableStateOf(false) }

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
            onClick = { navController.navigate("internship_details/$internshipId") }
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
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
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
                                text = stringResource(R.string.step_1_text_educational),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                    1 -> {

                        Column (modifier = Modifier.weight(0.7f)) {
                            Row (
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
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
                                text = stringResource(R.string.step_2_text_educational),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )

                            radioOptions.forEach { resId ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = selectedOption == resId,
                                        onClick = { selectedOption = resId }
                                    )
                                    Text(
                                        text = stringResource(id = resId),
                                    )
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        internshipViewModel.fetchAgreement({}, internship.id, internship.agreementName, context)
                                    },
                                ) {
                                    Text(text = stringResource(R.string.download_the_agreement))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button (
                                onClick = {
                                    if (selectedOption == R.string.accept) {
                                        messagingService.sendNotificationToUser(internship.userId, context.getString(R.string.internship_agreement), context.getString(R.string.the_internship_agreement_has_been_validated_by_your_educational_institution))
                                        internshipViewModel.setStep(internship.id, 2)
                                        navController.navigate("agreement/$internshipId")
                                    } else {
                                        messagingService.sendNotificationToUser(internship.userId, context.getString(R.string.internship_agreement), context.getString(R.string.the_internship_agreement_has_been_refused_by_your_educational_institution))
                                        internshipViewModel.setStep(internship.id, 0)
                                        navController.navigate("agreement/$internshipId")
                                    }
                                },
                                enabled = selectedOption != null
                            ){
                                Text(text = stringResource(R.string.next))
                            }
                        }
                    }
                    2 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
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
                                text = stringResource(R.string.step_3_text_educational),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )
                        }
                    }
                    3 -> {
                        Column (modifier = Modifier.weight(0.7f)) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
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
                                text = stringResource(R.string.step_4_text_educational),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify,
                            )

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        internshipViewModel.fetchAgreement({}, internship.id, internship.agreementName, context)
                                    },
                                ) {
                                    Text(text = stringResource(R.string.download_the_agreement))
                                }
                            }

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
                                    messagingService.sendNotificationToUser(internship.userId, context.getString(R.string.internship_agreement), context.getString(R.string.your_educational_institution_has_finalized_the_internship_agreement_you_can_now_start_your_internship))
                                    internshipViewModel.setStep(internship.id, 4)
                                    internshipViewModel.setAgreementName(internship.id, agreementFileName)
                                    internshipViewModel.setStatus(internship.id, "in_progress")
                                    applicationViewModel.deleteApplicationByUserAndOffer(internship.userId, internship.offerId)
                                    navController.navigate("internship_details/$internshipId")
                                },
                                enabled = uploaded.value
                            ){
                                Text(text = stringResource(R.string.finalize))
                            }
                        }
                    }
                }
            }
        }
    }
}