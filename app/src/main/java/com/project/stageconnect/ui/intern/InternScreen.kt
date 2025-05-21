package com.project.stageconnect.ui.intern

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.stageconnect.R
import com.project.stageconnect.model.User

@Composable
fun InternScreen(currentUser: User, navController: NavHostController, onLogout: () -> Unit, onUpdated: () -> Unit) {
    val items = listOf(
        BottomNavItem.Offers,
        BottomNavItem.Applications,
        BottomNavItem.Internship,
        BottomNavItem.Messages,
        BottomNavItem.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = stringResource(id = item.labelRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = item.labelRes),
                                fontSize = (9.5).sp
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Offers.route,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 16.dp)
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.Offers.route) { InternOffersScreen(currentUser, navController) }
            composable(BottomNavItem.Applications.route) { InternApplicationsScreen(currentUser, navController) }
            composable(BottomNavItem.Internship.route) { InternInternshipScreen() }
            composable(BottomNavItem.Messages.route) { InternMessagesScreen() }
            composable(BottomNavItem.Account.route) { InternAccountScreen(currentUser, navController, onLogout) }

            composable("account_edition") { InternAccountEditionScreen(currentUser, navController, onUpdated) }
            composable("offer_details/{offerId}") { InternOfferDetailsScreen(currentUser, navController, it.arguments?.getString("offerId")) }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: Int, val labelRes: Int) {
    data object Offers : BottomNavItem("offers", R.drawable.mail, R.string.offers)
    data object Applications : BottomNavItem("applications", R.drawable.folder, R.string.applications)
    data object Internship : BottomNavItem("internship", R.drawable.internship, R.string.internship)
    data object Messages : BottomNavItem("messages", R.drawable.chat, R.string.messages)
    data object Account : BottomNavItem("account", R.drawable.person, R.string.account)
}