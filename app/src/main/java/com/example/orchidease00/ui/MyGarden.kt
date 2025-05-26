package com.example.orchidease00.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orchidease00.data.local.MyOrchid
import java.sql.Date

@Composable
fun MyGardenScreen(myOrchids: List<MyOrchid>,
                   onOrchidClick: (orchidId: Int) -> Unit) {
    if (myOrchids.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Non hai ancora le orchidee", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(myOrchids) { orchid ->
                OrchidListItem(orchid = orchid, onClick = { onOrchidClick(orchid.id) })
                Divider()
            }
        }
    }
}

@Composable
fun OrchidListItem(orchid: MyOrchid, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(text = orchid.name, style = MaterialTheme.typography.titleMedium)
        if (orchid.customName.isNotBlank()) {
            Text(
                text = "Nome: ${orchid.customName}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
