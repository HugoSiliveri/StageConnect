package com.project.stageconnect.ui.intern

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.stageconnect.R
import com.project.stageconnect.model.Chat
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.ChatViewModel
import com.project.stageconnect.viewmodel.UserViewModel
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Vue des conversations d'un utilisateur.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue des conversations d'un utilisateur.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternMessagesScreen(currentUser: User, navController: NavController) {
    val chatViewModel: ChatViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf<List<User>>(emptyList())}
    var chats by remember { mutableStateOf<List<Chat>>(emptyList())}
    var chatUsersMap by remember { mutableStateOf<Map<Chat, User>>(emptyMap())}

    LaunchedEffect(Unit) {
        chatViewModel.loadChats({ list ->
            chats = list
            val userIds = list.flatMap { it.userIds }.distinct()
            userViewModel.loadUsers({ list2 ->
                users = list2
                val map = list2.associateBy { user ->
                    userIds.find { it == user.uid }
                }
                chatUsersMap = chats.associateWith { chat ->
                    map[chat.userIds.find { it != currentUser.uid }] as User
                }
            }, userIds)
        }, currentUser.uid)
    }

    val filteredChats = chatUsersMap.filter { mapEntry ->
        searchQuery.isBlank() || listOf(
            mapEntry.value.firstname,
            mapEntry.value.lastname,
            mapEntry.value.structname,
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { isActive = false },
            active = false,
            onActiveChange = { isActive = it },
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
        ) {}

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredChats.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_chats_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(filteredChats.entries.toList()) { index, (chat, user) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("chat/${user.uid}") }
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                if (user.type == "intern") {
                                    Text(
                                        text = "${user.firstname} ${user.lastname}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                } else {
                                    Text(
                                        text = user.structname,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = chat.lastMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp),)
                    }
                }
            }
        }
    }
}