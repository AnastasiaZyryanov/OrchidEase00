package com.example.orchidease00.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.orchidease00.data.domain.model.OrchidEvent
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun CustomCalendar(
    currentMonth: YearMonth,
    events: Map<LocalDate, List<OrchidEvent>>,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
    val dates = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (day in 1..daysInMonth) {
            add(currentMonth.atDay(day))
        }
    }

    Column {

        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercaseChar() } + " ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )


        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("L", "M", "M", "G", "V", "S", "D").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(4.dp))


        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp),
            userScrollEnabled = false
        ) {
            items(dates.size) { index ->
                val date = dates[index]
                val hasEvent = date != null && events.containsKey(date)
                val isSelected = date == selectedDate

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            }
                        )
                        .clickable(enabled = date != null) {
                            date?.let { onDateSelected(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = if (isSelected) Color.White else Color.Unspecified
                        )
                        if (hasEvent) {
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .background(Color.Red, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}