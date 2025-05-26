package com.example.orchidease00.ui

import android.app.Activity
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
import java.time.LocalDate
import java.util.*
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.local.MyOrchid
import kotlinx.coroutines.delay


@Composable
fun AddToGardenScreen(
    catalogItems: List<OrchidCatalogItem>,
    onSave: (MyOrchid) -> Unit,
    ) {
    Log.d("AddToGardenScreen", "Schermo compose")

    var selectedCatalogItem by remember { mutableStateOf<OrchidCatalogItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var enteredCustomName by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf<LocalDate?>(null) }
    var repotDate by remember { mutableStateOf<LocalDate?>(null) }
    var bloomDate by remember { mutableStateOf<LocalDate?>(null) }
    var lastWatered by remember { mutableStateOf<LocalDate?>(null) }
    var nextWatering by remember { mutableStateOf<LocalDate?>(null) }
    var lastFertilizing by remember { mutableStateOf<LocalDate?>(null) }
    var nextFertilizing by remember { mutableStateOf<LocalDate?>(null) }

    val openDatePicker = rememberDatePickerLauncher()
    val imageUris = remember { mutableStateListOf<Uri>() }

    val context = LocalContext.current
    var imagePath by remember { mutableStateOf("") }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {

            tempImageUri = it
            imagePath = saveCompressedImageToInternalStorage(context, it) ?: ""

          /*  val savedPath = saveImageToInternalStorage(context, it) // Используем вашу функцию
            if (savedPath != null) {
                imagePath = savedPath // Обновляем состояние
            } else {
                Text("Errore")
            }

           */
        }
    }
    /*
    val pickImagesLauncher = rememberLauncherForActivityResult(
       // contract = ActivityResultContracts.PickMultipleVisualMedia()
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uris ->
        uris?.let {
            imageUris.clear()
            imageUris.add(it)
            //   imageUris.addAll(uris)
        }
    }
         */
        /*
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris != null) { //always true
            val updatedList = imageUris.toMutableList().apply {
                addAll(uris)
            }
            imageUris.clear()
            imageUris.addAll(updatedList)
        }
    }

         */


    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        try {
            delay(100)
            focusRequester.requestFocus()
        } catch (e: Exception) {
            Log.e("FocusError", "Failed to request focus", e)
        }
    }

/*
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

 */

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
            selectedItem = selectedCatalogItem,
            onQueryChange = { searchQuery = it },
            onItemSelected = {
                selectedCatalogItem = it
                searchQuery = it.name
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = enteredCustomName,
            onValueChange = { enteredCustomName = it },
            label = { Text("Nome della orchidea (voluntariamente)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Spacer(Modifier.height(16.dp))


        OrchidPhotoManager(
            imagePath = imagePath,
            tempImageUri = tempImageUri,
        //    imageUris = imageUris,
            onImagesChanged = { newPath ->
                imagePath = newPath
            },
          /*  onImagesChanged = { newList ->
                imageUris.clear()
                imageUris.addAll(newList)
            },
           */
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

        Button(
            onClick = {
                try {
                    selectedCatalogItem?.let { item ->
                        val orchid = MyOrchid(
                            name = item.name,
                            customName = enteredCustomName,
                            filePath = imagePath,
                            //imageUris = imageUris.map { it.toString() },
                            orchidTypeId = item.id,
                            purchaseDate = purchaseDate,
                            repotDate = repotDate,
                            bloomDate = bloomDate,
                            lastWatered = lastWatered,
                            nextWatering = nextWatering,
                            lastFertilizing = lastFertilizing,
                            nextFertilizing = nextFertilizing
                        )
                        onSave(orchid)
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





