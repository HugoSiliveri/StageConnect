package com.project.stageconnect.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.model.User
import com.project.stageconnect.theme.StageConnectTheme
import com.project.stageconnect.viewmodel.SignupViewModel
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Composant de l'écran de création de compte.
 *
 * @param viewModel Le modèle de vue de création de compte.
 * @param onSignupSuccess La fonction à appeler lorsque la création de compte est réussie.
 * @param onLoginClick La fonction à appeler lorsque le bouton "Se connecter" est cliqué.
 *
 * @return Le composant de l'écran de création de compte.
 */
@Composable
fun SignupScreen(viewModel: SignupViewModel, onSignupSuccess: () -> Unit, onLoginClick: () -> Unit) {
    val signupState by viewModel.signupState.collectAsState()
    val userViewModel: UserViewModel = viewModel()

    val context = LocalContext.current

    var selectedType by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var institutions by remember { mutableStateOf<List<User>>(emptyList()) }

    val intern = stringResource(R.string.intern)
    val company = stringResource(R.string.company)
    val educational = stringResource(R.string.educational_institution)

    LaunchedEffect(Unit) {
        userViewModel.loadEducationalInstitutions { list ->
            institutions = list
        }
    }

    var selectedItem by remember { mutableStateOf("") }
    var expandedType by remember { mutableStateOf(false) }
    var expandedInstitution by remember { mutableStateOf(false) }

    StageConnectTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.mipmap.logo_foreground),
                            contentDescription = "Logo StageConnect",
                            modifier = Modifier
                                .size(124.dp)
                                .offset(x = (-12).dp),
                            tint = Color.Unspecified
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .offset(x = (-32).dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Box {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.user_type)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        listOf(stringResource(R.string.intern), stringResource(R.string.company), stringResource(R.string.educational_institution)
                        ).forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier
                        .matchParentSize()
                        .clickable { expandedType = true })
                }

                when (selectedType) {
                    stringResource(R.string.intern) -> {
                        OutlinedTextField(
                            value = firstname,
                            onValueChange = { firstname = it },
                            label = { Text(stringResource(R.string.first_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = lastname,
                            onValueChange = { lastname = it },
                            label = { Text(stringResource(R.string.last_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(stringResource(R.string.phone)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(stringResource(R.string.address)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box {
                            OutlinedTextField(
                                value = selectedItem,
                                onValueChange = {},
                                label = { Text(stringResource(R.string.educational_institution)) },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true
                            )
                            DropdownMenu(
                                expanded = expandedInstitution,
                                onDismissRequest = { expandedInstitution = false },
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                institutions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.structname) },
                                        onClick = {
                                            selectedItem = option.structname
                                            expandedInstitution = false
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier
                                .matchParentSize()
                                .clickable { expandedInstitution = true }
                            )
                        }
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    stringResource(R.string.company) -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(stringResource(R.string.name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(stringResource(R.string.phone)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(stringResource(R.string.address)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    stringResource(R.string.educational_institution) -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(stringResource(R.string.name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.email)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(stringResource(R.string.phone)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(stringResource(R.string.address)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.auth_log_in_text),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable {
                            onLoginClick()
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val typeKey = when (selectedType) {
                            intern -> "intern"
                            company -> "company"
                            educational -> "educational"
                            else -> "unknown"
                        }
                        viewModel.signup(typeKey, email, password, firstname, lastname, name, phone, address, institutions.find { it.structname == selectedItem }?.uid)
                    },
                    enabled = signupState != DataResult.Loading
                ) {
                    Text(stringResource(R.string.action_sign_in_short))
                }

                if (signupState is DataResult.Error) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LaunchedEffect((signupState as DataResult.Error).message) {
                        Toast.makeText(context, context.getString(R.string.error_message) + (signupState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                    }
                }

                if (signupState == DataResult.Success) {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, context.getString(R.string.account_created) , Toast.LENGTH_SHORT).show()
                        onSignupSuccess()
                    }
                }
            }
        }
    }
}