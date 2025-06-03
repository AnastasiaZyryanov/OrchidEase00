package com.example.orchidease00.ui.nav

import kotlinx.coroutines.CoroutineScope
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.orchidease00.R
import com.example.orchidease00.ui.garden.OrchidViewModel
import kotlinx.coroutines.launch


enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Giardino(title = R.string.giardino),
    Cambia(title = R.string.cambia_info),
    Calendar(title = R.string.calendario),
    Aggiungi(title = R.string.aggiungi),
    Cerca(title = R.string.cerca),
    Dettagli(title = R.string.dettagli),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrchidTopBar(drawerState: DrawerState, scope: CoroutineScope,
                 currentScreen: AppScreen, canNavigateBack: Boolean,
                 navigateUp: () ->Unit ) {

    TopAppBar(
        title = {
            Text(stringResource(currentScreen.title), color = Color.White)
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon ( 
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
                            }
            
            else {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
            }
        },
        colors =  TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFC89FA3)
        )
    )
}
@Composable
fun ScreenBlock(text: String, painter: Painter, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC89FA3)),
            modifier = Modifier.size(70.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun OrchidApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route
            ?.substringBefore("/")
            ?: AppScreen.Home.name
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreen.Home.name)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Il mio giardino") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreen.Giardino.name)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Aggiungi") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreen.Aggiungi.name)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Calendar") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreen.Calendar.name)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Cerca nel catalogo") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(AppScreen.Cerca.name)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                OrchidTopBar(drawerState, scope, currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                    )
            })
        { innerPadding ->
            NavHostContainer(
                navController = navController,
                modifier = Modifier.padding((innerPadding))
                    .fillMaxSize()
            )


        }
    }
}

