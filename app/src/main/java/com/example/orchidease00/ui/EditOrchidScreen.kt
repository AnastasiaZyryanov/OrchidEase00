package com.example.orchidease00.ui


import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.ui.OrchidViewModel
import com.example.orchidease00.data.local.MyOrchid
import java.io.File
import java.time.LocalDate


@Composable
fun EditOrchidScreen(
    orchidId: Int,
    catalogItems: List<OrchidCatalogItem>,
    viewModel: OrchidViewModel,
    onSaveComplete: () -> Unit,
    onDelete: (MyOrchid) -> Unit,
    onCatalogOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("EditOrchidScreen", "OrchidId = $orchidId")

    val orchid by viewModel.getMyOrchidById(orchidId).collectAsState(initial = null)
    val myOrchid = orchid

    orchid?.let { orchidData ->
        var selectedCatalogItem by remember {
            mutableStateOf(catalogItems.find { it.id == orchidData.orchidTypeId })
        }
        var searchQuery by remember { mutableStateOf(selectedCatalogItem?.name ?: "") }
        var customName by remember { mutableStateOf(orchidData.customName) }
        val imageUris = remember { mutableStateListOf<Uri>() }
        //Carica le imagini da room una volta sola
            /* LaunchedEffect(orchid?.id) {
            imageUris.clear()
            orchid?.imageUris
                ?.mapNotNull {
                    try { Uri.parse(it) } catch (e: Exception) { null }
                }
                ?.let { imageUris.addAll(it) }
        }
             */
            /* val imageUris = remember {
            mutableStateListOf<Uri>().apply {
                orchidData.imageUris?.mapNotNull {
                    try {
                        Uri.parse(it)
                    } catch (e: Exception) {
                        null
                    }
                }?.let { addAll(it) }
            }
        }
        */
            /*
        val pickImagesLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->
            if (uris != null) {
                val updatedList = imageUris.toMutableList().apply {
                    addAll(uris)
                }
                imageUris.clear()
                imageUris.addAll(updatedList)
            }
        }
                     */
        val context = LocalContext.current
        var currentImagePath by remember { mutableStateOf(orchidData.filePath) }
        var tempImageUri by remember { mutableStateOf<Uri?>(null) }

        val pickImagesLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                tempImageUri = it
                currentImagePath = saveCompressedImageToInternalStorage(context, it) ?: ""
            }
        }
            /*
        val pickImagesLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->
            if (uris != null) {
                val internalUris = uris.mapNotNull { uri ->
                    saveImageToInternalStorage(context, uri)
                }
                val newImageUris = internalUris.map { Uri.fromFile(File(it)) }

                imageUris.clear()
                imageUris.addAll(newImageUris)
            }
        }
          */


        var purchaseDate by remember { mutableStateOf(orchidData.purchaseDate) }
        var repotDate by remember { mutableStateOf(orchidData.repotDate) }
        var bloomDate by remember { mutableStateOf(orchidData.bloomDate) }
        var lastWatered by remember { mutableStateOf(orchidData.lastWatered) }
        var nextWatering by remember { mutableStateOf(orchidData.nextWatering) }
        var lastFertilizing by remember { mutableStateOf(orchidData.lastFertilizing) }
        var nextFertilizing by remember { mutableStateOf(orchidData.nextFertilizing) }

        val openDatePicker = rememberDatePickerLauncher()


        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Edit orchid", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))
                /*
            CatalogNameAutocompleteField(
                catalogItems = catalogItems,
                selectedItem = selectedCatalogItem,
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onItemSelected = { selectedCatalogItem = it },
                modifier = Modifier
            )
                             */

            Text(
                text = "Tipo selezionato attualmente: ${orchidData.name}",
                style = MaterialTheme.typography.bodyMedium
            )

            val currentCatalogItem = catalogItems.find { it.id == orchidData.orchidTypeId }

            if (currentCatalogItem != null) {
                TextButton(onClick = {
                    Log.d("EditOrchidScreen", "Navigating to catalog for name: ${currentCatalogItem.name}")
                    onCatalogOpen(Uri.encode(currentCatalogItem.name))
                }) {
                    Text("Visualizza nel catalogo")
                }
            }

            Spacer(Modifier.height(8.dp))

            CatalogNameAutocompleteField(
                catalogItems = catalogItems,
                selectedItem = selectedCatalogItem,
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onItemSelected = { selectedCatalogItem = it },
                modifier = Modifier
            )
            if (selectedCatalogItem != null) {
                TextButton(onClick = {
                    val encodedName = Uri.encode(selectedCatalogItem!!.name)
                    onCatalogOpen(encodedName)
                }) {
                    Text("Visualizza nel catalogo")
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Nome della orchidea (voluntariamente)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OrchidPhotoManager(
                imagePath = currentImagePath,
                tempImageUri = tempImageUri,
              //  imageUris = imageUris,
              /*  onImagesChanged = { newList ->
                    imageUris.clear()
                    imageUris.addAll(newList)
                },
                               */
                onImagesChanged = { newPath ->
                    currentImagePath = newPath
                },
                onPickImagesClicked = {
                    pickImagesLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )


            Spacer(Modifier.height(16.dp))



            DateField("Data del'aquisto", purchaseDate) { openDatePicker { purchaseDate = it } }
            DateField("Data del trapianto", repotDate) { openDatePicker { repotDate = it } }
            DateField("Data della fioritura", bloomDate) { openDatePicker { bloomDate = it } }
            DateField("Ultima irrigazione", lastWatered) { openDatePicker { lastWatered = it } }
            DateField("Prossima irrigazione", nextWatering) { openDatePicker { nextWatering = it } }
            DateField("Ultima fertilizzazione", lastFertilizing) {
                openDatePicker {
                    lastFertilizing = it
                }
            }
            DateField("Prossima fertilizzazione", nextFertilizing) {
                openDatePicker {
                    nextFertilizing = it
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (selectedCatalogItem != null) {
                            val updated = orchidData.copy(
                                name = selectedCatalogItem!!.name,
                                customName = customName,
                                filePath = currentImagePath,
                               // imageUris = imageUris.map { it.toString() },
                                orchidTypeId = selectedCatalogItem!!.id,
                                purchaseDate = purchaseDate,
                                repotDate = repotDate,
                                bloomDate = bloomDate,
                                lastWatered = lastWatered,
                                nextWatering = nextWatering,
                                lastFertilizing = lastFertilizing,
                                nextFertilizing = nextFertilizing
                            )
                            viewModel.updateMyOrchid(updated)
                            onSaveComplete()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salva")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.deleteMyOrchid(orchidData)
                        onDelete(orchidData)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cancella")
                }

            }

        } ?: run {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}




