package com.example.orchidease00

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.orchidease00.data.AppUiState
import com.example.orchidease00.ui.AddToGardenScreen
import com.example.orchidease00.ui.CalendarScreen
import com.example.orchidease00.ui.CatalogScreen
import com.example.orchidease00.ui.HomeScreen
import com.example.orchidease00.ui.MyGardenScreen
import com.example.orchidease00.ui.OrchidDetailScreen
import com.example.orchidease00.ui.OrchidCatalogViewModel
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.local.MyOrchidDatabase
import com.example.orchidease00.ui.CalendarViewModel
import com.example.orchidease00.ui.CalendarViewModelFactory
import com.example.orchidease00.ui.EditOrchidScreen
import com.example.orchidease00.ui.OrchidViewModel
import io.ktor.websocket.Frame


@Composable
fun NavHostContainer(navController: NavHostController,
                     modifier: Modifier) {
    NavHost(navController = navController, startDestination = "home",
        modifier = modifier) {
        composable(AppScreen.Home.name) { HomeScreen(modifier, navController) }
        composable(AppScreen.Giardino.name) {
            val viewModel: OrchidViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            MyGardenScreen(
                myOrchids =  uiState.myOrchids,
                onOrchidClick = { id -> navController.navigate("${AppScreen.Cambia.name}/$id") }
                    )
            }
        composable(
            "${AppScreen.Cambia.name}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id")!!
            val viewModel: OrchidViewModel = viewModel()

            val uiState = viewModel.uiState.collectAsState().value
            val catalogItems = uiState.orchids
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val database = MyOrchidDatabase.getDatabase(application)
            val dao = database.myOrchidDao()
            val factory = CalendarViewModelFactory(dao)
            val calendarViewModel: CalendarViewModel = viewModel(factory = factory)

            EditOrchidScreen(
                orchidId = id,
                catalogItems = catalogItems,
                viewModel = viewModel,
                onSaveComplete = { navController.popBackStack() },
                onDelete = { orchid ->
                    viewModel.deleteMyOrchid(orchid)
                    navController.popBackStack()
                },
                onCatalogOpen = { name ->
                    navController.navigate("${AppScreen.Dettagli.name}/${Uri.encode(name)}")
                },
                calendarViewModel=calendarViewModel
            )
        }

        composable(AppScreen.Calendar.name) {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val database = MyOrchidDatabase.getDatabase(application)
            val dao = database.myOrchidDao()
            val factory = CalendarViewModelFactory(dao)
            val viewModel: CalendarViewModel = viewModel(factory = factory)

            CalendarScreen(viewModel = viewModel)
        }



        composable(AppScreen.Aggiungi.name) {
            val viewModel: OrchidViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current
            val application = context.applicationContext as Application
            val database = MyOrchidDatabase.getDatabase(application)
            val dao = database.myOrchidDao()
            val factory = CalendarViewModelFactory(dao)
            val calendarViewModel: CalendarViewModel = viewModel(factory = factory)
            AddToGardenScreen(
                catalogItems = uiState.orchids,
                onSave = { newOrchid ->
                    viewModel.insertMyOrchid(newOrchid)
                    navController.popBackStack()
                },
                calendarViewModel=calendarViewModel
            ) }

            composable(AppScreen.Cerca.name) {
            val catalogViewModel: OrchidCatalogViewModel = viewModel()
            val appUiState by catalogViewModel.uiState.collectAsState()
            CatalogScreen(uiState = appUiState.catalogUiState,
            onOrchidClick = { orchid -> val encodedName = Uri.encode(orchid.name.trim())
                Log.d("CatalogScreen", "Passagio per i detagli: $encodedName")
                navController.navigate("${AppScreen.Dettagli.name}/$encodedName")
            }) }
        composable("${AppScreen.Dettagli.name}/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.trim()?.let { Uri.decode(it) } ?: ""
            Log.d("DettagliScreen", "Ricevuto il nome della orchidea: [$name]")
            OrchidDetailScreen(name = name)
        }

    }
}