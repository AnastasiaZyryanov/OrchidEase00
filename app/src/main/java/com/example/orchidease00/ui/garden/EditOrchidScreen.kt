package com.example.orchidease00.ui.garden

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import com.example.orchidease00.data.local.MyOrchid
import com.example.orchidease00.ui.calendar.CalendarViewModel
import kotlinx.coroutines.delay



@Composable
fun EditOrchidScreen(
    orchidId: Int,
    catalogItems: List<OrchidCatalogItem>,
    viewModel: OrchidViewModel,
    calendarViewModel: CalendarViewModel,
    onSaveComplete: () -> Unit,
    onDelete: (MyOrchid) -> Unit,
    onCatalogOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orchid by viewModel.getMyOrchidById(orchidId).collectAsState(initial = null)
    val context = LocalContext.current

    orchid?.let { orchidData ->

        var selectedCatalogItem by remember {
            mutableStateOf(catalogItems.find { it.id == orchidData.orchidTypeId })
        }
        var searchQuery by remember { mutableStateOf(selectedCatalogItem?.name ?: "") }

        var customName by remember { mutableStateOf(orchidData.customName) }

        var imagePaths by remember { mutableStateOf(orchidData.imagePaths ?: emptyList()) }
        var tempImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

        var purchaseDate by remember { mutableStateOf(orchidData.purchaseDate) }
        var repotDate by remember { mutableStateOf(orchidData.repotDate) }
        var bloomDate by remember { mutableStateOf(orchidData.bloomDate) }
        var lastWatered by remember { mutableStateOf(orchidData.lastWatered) }
        var lastFertilizing by remember { mutableStateOf(orchidData.lastFertilizing) }
        var nextWateringMillis by remember { mutableStateOf(orchidData.nextWatering) }
        var nextFertilizingMillis by remember { mutableStateOf(orchidData.nextFertilizing) }

        val openDatePicker = rememberDatePickerLauncher()

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
            delay(300)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Modifica orchidea", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Text("Tipo selezionato attualmente: ${orchidData.name}", style = MaterialTheme.typography.bodyMedium)

            selectedCatalogItem?.let {
                TextButton(onClick = { onCatalogOpen(Uri.encode(it.name)) }) {
                    Text("Visualizza nel catalogo")
                }
            }

            Spacer(Modifier.height(8.dp))

            CatalogNameAutocompleteField(
                catalogItems = catalogItems,
                searchQuery = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.isBlank()) selectedCatalogItem = null
                },
                onItemSelected = {
                    selectedCatalogItem = it
                    searchQuery = it.name
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

             OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Nome della orchidea (volontariamente)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )
            )


            Spacer(Modifier.height(16.dp))



            OrchidPhotoManager(
                imagePaths = imagePaths,
                tempImageUris = tempImageUris,
                onImagesChanged = { imagePaths = it },
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

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        val updated = orchidData.copy(
                            name = selectedCatalogItem?.name ?: orchidData.name,
                            orchidTypeId = selectedCatalogItem?.id ?: orchidData.orchidTypeId,
                            customName = customName,
                            imagePaths = imagePaths,
                            purchaseDate = purchaseDate,
                            repotDate = repotDate,
                            bloomDate = bloomDate,
                            lastWatered = lastWatered,
                            lastFertilizing = lastFertilizing,
                            nextWatering = nextWateringMillis,
                            nextFertilizing = nextFertilizingMillis
                        )

                        viewModel.updateMyOrchid(updated)

                        nextWateringMillis?.let { millis ->
                            calendarViewModel.scheduleOrchidReminder(
                                context = context,
                                orchidId = updated.id,
                                orchidName = updated.customName.ifBlank { updated.name },
                                triggerAtMillis = millis
                            )
                        }

                        nextFertilizingMillis?.let { millis ->
                            calendarViewModel.scheduleOrchidReminder(
                                context = context,
                                orchidId = updated.id,
                                orchidName = updated.customName.ifBlank { updated.name },
                                triggerAtMillis = millis
                            )
                        }

                        onSaveComplete()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salva")
                }
                var showDialog by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Elimina")
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Conferma eliminazione") },
                        text = { Text("Sei sicuro di voler eliminare questa orchidea?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.deleteMyOrchid(orchidData)
                                    onDelete(orchidData)
                                    onSaveComplete()
                                    showDialog = false
                                }
                            ) {
                                Text("Sì")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Annulla")
                            }
                        }
                    )
                }

            }
        }
    }
}
