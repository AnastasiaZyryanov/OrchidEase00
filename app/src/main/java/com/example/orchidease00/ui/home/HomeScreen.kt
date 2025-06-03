package com.example.orchidease00.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.orchidease00.ui.nav.AppScreen
import com.example.orchidease00.R
import com.example.orchidease00.ui.nav.ScreenBlock

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "OrchidEase",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7B2D3A),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ScreenBlock("Giardino", painterResource(R.drawable.orchid)) {
                    navController.navigate(AppScreen.Giardino.name)
                }
                ScreenBlock("Calendario", painterResource(R.drawable.calendar_month_24dp_000000_fill0_wght400_grad0_opsz24)) {
                    navController.navigate(AppScreen.Calendar.name)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ScreenBlock("Aggiungi", painterResource(R.drawable.add_2_24dp_000000_fill0_wght400_grad0_opsz24)) {
                    navController.navigate(AppScreen.Aggiungi.name)
                }
                ScreenBlock("Catalogo", painterResource(R.drawable.search_24dp_000000_fill0_wght400_grad0_opsz24)) {
                    navController.navigate(AppScreen.Cerca.name)
                }
            }
        }
    }
}
