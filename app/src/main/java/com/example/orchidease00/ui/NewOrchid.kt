package com.example.orchidease00.ui

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.util.*
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.local.MyOrchid
import com.example.orchidease00.data.local.MyOrchidDatabase
import kotlinx.coroutines.delay



@Composable
fun AddToGardenScreen(
    catalogItems: List<OrchidCatalogItem>,
    onSave: (MyOrchid) -> Unit,
    calendarViewModel: CalendarViewModel
) {
    Log.d("AddToGardenScreen", "Schermo compose")

    var selectedCatalogItem by remember { mutableStateOf<OrchidCatalogItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var customName by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf<LocalDate?>(null) }
    var repotDate by remember { mutableStateOf<LocalDate?>(null) }
    var bloomDate by remember { mutableStateOf<LocalDate?>(null) }
    var lastWatered by remember { mutableStateOf<LocalDate?>(null) }
    var lastFertilizing by remember { mutableStateOf<LocalDate?>(null) }
    var nextWateringMillis by remember { mutableStateOf<Long?>(null) }
    var nextFertilizingMillis by remember { mutableStateOf<Long?>(null) }

    val openDatePicker = rememberDatePickerLauncher()
    val context = LocalContext.current
    var imagePath by remember { mutableStateOf("") }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            tempImageUri = it
            imagePath = saveCompressedImageToInternalStorage(context, it) ?: ""
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        try {
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (e: Exception) {
            Log.e("FocusError", "Failed to request focus", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Scegli il tipo della orchidea", style = MaterialTheme.typography.titleMedium)


        CatalogNameAutocompleteField(
            catalogItems = catalogItems,
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            onItemSelected = {
                selectedCatalogItem = it
                searchQuery = it.name
            },
            modifier = Modifier
                .fillMaxWidth()

        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = customName,
            onValueChange = { customName = it },
            label = { Text("Nome della orchidea (volontariamente)") },
            modifier = Modifier.fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                autoCorrect = true,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )

        )

        Spacer(Modifier.height(16.dp))

        OrchidPhotoManager(
            imagePath = imagePath,
            tempImageUri = tempImageUri,
            onImagesChanged = { newPath -> imagePath = newPath },
            onPickImagesClicked = {
                pickImagesLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        DateField("Data d’acquisto", purchaseDate) { openDatePicker { purchaseDate = it } }
        DateField("Data del trapianto", repotDate) { openDatePicker { repotDate = it } }
        DateField("Data della fioritura", bloomDate) { openDatePicker { bloomDate = it } }
        DateField("Ultima irrigazione", lastWatered) { openDatePicker { lastWatered = it } }
        DateField("Ultima fertilizzazione", lastFertilizing) { openDatePicker { lastFertilizing = it } }

        Spacer(Modifier.height(16.dp))

        DateTimePickerField(
            label = "Prossima irrigazione (con orario)",
            selectedDateTime = nextWateringMillis,
            onDateTimeSelected = { nextWateringMillis = it }
        )

        DateTimePickerField(
            label = "Prossima fertilizzazione (con orario)",
            selectedDateTime = nextFertilizingMillis,
            onDateTimeSelected = { nextFertilizingMillis = it }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                try {
                    selectedCatalogItem?.let { item ->
                        val orchid = MyOrchid(
                            name = item.name,
                            customName = customName,
                            filePath = imagePath,
                            orchidTypeId = item.id,
                            purchaseDate = purchaseDate,
                            repotDate = repotDate,
                            bloomDate = bloomDate,
                            lastWatered = lastWatered,
                            nextWatering = nextWateringMillis,
                            lastFertilizing = lastFertilizing,
                            nextFertilizing = nextFertilizingMillis
                        )

                        onSave(orchid)

                        nextWateringMillis?.let {
                            calendarViewModel.scheduleOrchidReminder(
                                context,
                                "${orchid.customName.ifBlank { orchid.name }} — Watering",
                                it
                            )
                        }
                        nextFertilizingMillis?.let {
                            calendarViewModel.scheduleOrchidReminder(
                                context,
                                "${orchid.customName.ifBlank { orchid.name }} — Fertilizing",
                                it
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AddToGarden", "Errore in salvataggio", e)
                }
            },
            enabled = selectedCatalogItem != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva")
        }
    }
}
