package com.project.stageconnect.ui.company

import android.util.Log
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.stageconnect.R
import com.project.stageconnect.model.User

@Composable
fun CompanyScreen(currentUser: User, navController: NavHostController, onLogout: () -> Unit, onUpdated: () -> Unit) {
    val items = listOf(
        BottomNavItem.Offers,
        BottomNavItem.Applications,
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
                            Text(stringResource(id = item.labelRes))
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
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.Offers.route) { CompanyOffersScreen(currentUser, navController) }
            composable(BottomNavItem.Applications.route) { CompanyApplicationsScreen() }
            composable(BottomNavItem.Messages.route) { CompanyMessagesScreen() }
            composable(BottomNavItem.Account.route) { CompanyAccountScreen(currentUser, navController, onLogout) }

            composable("add_offer") { CompanyOfferCreationScreen(currentUser, navController) }
            composable("offer_details/{offerId}") { CompanyOfferDetailsScreen(navController, it.arguments?.getString("offerId")) }
            composable("account_edition") { CompanyAccountEditionScreen(currentUser, navController, onUpdated) }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: Int, val labelRes: Int) {
    data object Offers : BottomNavItem("offers", R.drawable.mail, R.string.offers)
    data object Applications : BottomNavItem("applications", R.drawable.folder, R.string.applications)
    data object Messages : BottomNavItem("messages", R.drawable.chat, R.string.messages)
    data object Account : BottomNavItem("account", R.drawable.person, R.string.account)
}


