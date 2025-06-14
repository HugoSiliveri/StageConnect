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
import com.project.stageconnect.R
import com.project.stageconnect.model.DataResult
import com.project.stageconnect.theme.StageConnectTheme
import com.project.stageconnect.viewmodel.LoginViewModel

/**
 * Composant de l'écran de connexion.
 *
 * @param viewModel Le modèle de vue de connexion.
 * @param onLoginSuccess La fonction à appeler lorsque la connexion est réussie.
 * @param onSignUpClick La fonction à appeler lorsque le bouton "Créer un compte" est cliqué.
 *
 * @return Le composant de l'écran de connexion.
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, onSignUpClick: () -> Unit) {
    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.action_sign_in_short),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable {
                            onSignUpClick()
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    enabled = loginState != DataResult.Loading
                ) {
                    Text(stringResource(R.string.auth_log_in_text))
                }

                if (loginState is DataResult.Error) {
                    Spacer(modifier = Modifier.height(12.dp))

                    LaunchedEffect((loginState as DataResult.Error).message) {
                        Toast.makeText(context, context.getString(R.string.error_message) + (loginState as DataResult.Error).message, Toast.LENGTH_SHORT).show()
                    }
                }

                if (loginState == DataResult.Success) {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, context.getString(R.string.connexion_successful) , Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                }
            }
        }
    }
}