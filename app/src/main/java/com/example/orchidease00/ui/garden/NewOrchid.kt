package com.example.orchidease00.ui.garden

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import java.time.LocalDate
import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import com.example.orchidease00.data.domain.model.OrchidEvent
import com.example.orchidease00.data.local.MyOrchid
import com.example.orchidease00.ui.calendar.CalendarViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId


@Composable
fun AddToGardenScreen(
    catalogItems: List<OrchidCatalogItem>,
    onSave: (MyOrchid) -> Unit,
    calendarViewModel: CalendarViewModel
) {

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
    var imagePaths by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var tempImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newImagePaths = uris.mapNotNull {
                saveCompressedImageToInternalStorage(context, it)
            }

            tempImageUris = uris

            imagePaths = imagePaths + newImagePaths

            if (imagePaths.size > 5) {
                imagePaths = imagePaths.take(5)
                tempImageUris = tempImageUris.take(5)
            }
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
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
            imagePaths = imagePaths,
            tempImageUris = tempImageUris,
            onImagesChanged = { updatedList ->
                imagePaths = updatedList
            },
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
                selectedCatalogItem?.let { item ->
                        val orchid = MyOrchid(
                            name = item.name,
                            customName = customName,
                            imagePaths =  imagePaths,
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

                        nextWateringMillis?.let { millis ->
                         calendarViewModel.scheduleOrchidReminder(
                                context = context,
                                orchidId = orchid.id,
                                orchidName = orchid.customName.ifBlank { orchid.name },
                                triggerAtMillis = millis
                            )
                        }

                        nextFertilizingMillis?.let { millis ->
                            calendarViewModel.scheduleOrchidReminder(
                                context = context,
                                orchidId = orchid.id,
                                orchidName = orchid.customName.ifBlank { orchid.name },
                                triggerAtMillis = millis
                            )
                        }
                    }

            },
            enabled = selectedCatalogItem != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva")
        }
    }
}
