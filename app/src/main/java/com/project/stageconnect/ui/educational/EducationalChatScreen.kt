package com.project.stageconnect.ui.educational

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.model.Chat
import com.project.stageconnect.model.Message
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.ChatViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.project.stageconnect.R
import com.project.stageconnect.ui.intern.MessageInputBar
import com.project.stageconnect.utils.NotificationService
import com.project.stageconnect.viewmodel.UserViewModel

/**
 * Vue d'une conversation entre deux utilisateurs.
 *
 * @param currentUser L'utilisateur actuel.
 * @param otherUserId L'identifiant de l'autre utilisateur.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue d'une conversation entre deux utilisateurs.
 */
@Composable
fun EducationalChatScreen(currentUser: User, otherUserId: String?, navController: NavController) {
    val chatViewModel: ChatViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val notificationService = NotificationService()

    var chat by remember { mutableStateOf<Chat?>(null) }
    var otherUser by remember { mutableStateOf<User?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        userViewModel.loadUser({ usr ->
            otherUser = usr

            chatViewModel.loadChat({ cht, msg ->
                chat = cht
                messages = msg
            }, currentUser.uid, usr?.uid ?: "")
        }, otherUserId ?: "")
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        otherUser?.let { otherUser ->
            Row (verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = { navController.navigate("messages") },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }

                if (otherUser.type == "intern") {
                    Text(
                        text = otherUser.firstname,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )

                    Text(
                        text = otherUser.lastname,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 6.dp),
                    )
                } else {
                    Text(
                        text = otherUser.structname,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                state = listState
            ) {
                items(messages) { message ->
                    val isCurrentUser = message.senderId == currentUser.uid
                    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
                    ) {
                        Surface(
                            shape = if (isCurrentUser) RoundedCornerShape(20.dp, 20.dp, 8.dp, 20.dp) else RoundedCornerShape(20.dp, 20.dp, 20.dp, 8.dp),
                            color = backgroundColor,
                            modifier = Modifier
                                .padding(8.dp)
                                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f)
                        ) {
                            Text(
                                text = message.content,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
            }

            MessageInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        val currentChatId = chat?.id
                        if (currentChatId != null) {
                            chatViewModel.sendMessage(currentChatId, currentUser.uid, messageText.trim())
                            if (currentUser.type == "intern") notificationService.sendNotificationToUser(otherUser.uid, currentUser.firstname + " " + currentUser.lastname, messageText.trim())
                            else notificationService.sendNotificationToUser(otherUser.uid, currentUser.structname, messageText.trim())
                        }
                        messageText = ""
                    }
                }
            )
        }
    }
}
