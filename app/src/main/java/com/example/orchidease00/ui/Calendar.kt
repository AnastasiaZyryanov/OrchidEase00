package com.example.orchidease00.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.IconButton
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orchidease00.data.local.MyOrchidDao


@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val events by viewModel.events.collectAsState()
    val eventsMap = remember(events) { events.groupBy { it.date } }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }

        CustomCalendar(
            currentMonth = currentMonth,
            events = eventsMap,
            onDateSelected = { selectedDate = it },
            selectedDate = selectedDate
        )

        Spacer(Modifier.height(16.dp))

        val selectedEvents = eventsMap[selectedDate].orEmpty()
        Text("Eventi per ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
        Spacer(Modifier.height(8.dp))

        if (selectedEvents.isNotEmpty()) {
            selectedEvents.forEach { event ->
                Text("• ${event.description} ${event.orchidName}")
            }
        } else {
            Text("Non ci sono gli eventi")
        }
    }
}
