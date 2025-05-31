package com.project.stageconnect.ui.company

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
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
import com.project.stageconnect.model.Offer
import com.project.stageconnect.model.User
import com.project.stageconnect.viewmodel.OfferViewModel
import com.project.stageconnect.utils.Utils

/**
 * Vue des offres d'emploi de l'entreprise.
 *
 * @param currentUser L'utilisateur actuel.
 * @param navController Le contrôleur de navigation.
 *
 * @return La vue des offres d'emploi de l'entreprise.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyOffersScreen(currentUser: User, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    val offerViewModel: OfferViewModel = viewModel()

    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }

    LaunchedEffect(Unit) {
        offerViewModel.loadNoInternshipCompanyOffers({ list ->
            offers = list
        }, currentUser.uid)
    }

    val filteredOffers = offers.filter { offer ->
        searchQuery.isBlank() || listOf(
            offer.title,
            offer.companyName,
            offer.location,
            offer.description
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

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

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { navController.navigate("add_offer") }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.add_new_offer))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredOffers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_offers_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.wrapContentHeight()) {
                itemsIndexed(filteredOffers) { index, offer ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .fillMaxWidth()
                            .clickable { navController.navigate("offer_details/${offer.id}") }
                    ) {
                        if (index == 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Text(text = offer.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "${offer.companyName} • ${Utils.extractPostalCodeAndCity(offer.location)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = offer.description.take(80),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (index != offers.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .height(8.dp)
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}