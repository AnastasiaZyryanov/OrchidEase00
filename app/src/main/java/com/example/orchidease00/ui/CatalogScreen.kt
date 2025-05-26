package com.example.orchidease00.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.orchidease00.data.OrchidCatalogItem
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CatalogItemRow(
    orchid: OrchidCatalogItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = orchid.name, style = MaterialTheme.typography.titleMedium)
            Text(text = orchid.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
    }
}

@Composable
fun CatalogScreen(
    uiState: OrchidCatalogUiState,
    onOrchidClick: (OrchidCatalogItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Ricerca per il nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is OrchidCatalogUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is OrchidCatalogUiState.Error -> {
                Text(
                    text = "Errore: ${uiState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is OrchidCatalogUiState.Success -> {
                Log.d("CatalogScreen", "Loaded ${uiState.orchids.size} orchids")

                val filteredList = uiState.orchids.filter {
                    it.name?.contains(searchQuery, ignoreCase = true) == true
                }
                if (filteredList.isEmpty()) {
                    Text("Non è trovato nulla", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn {
                        items(filteredList) { orchid ->
                            CatalogItemRow(orchid = orchid, onClick = { onOrchidClick(orchid) })
                        }
                    }
                }

            }
        }
    }
}
