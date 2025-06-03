package com.example.orchidease00.ui.garden

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.LocalDate
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import java.util.*
import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CatalogNameAutocompleteField(
    catalogItems: List<OrchidCatalogItem>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onItemSelected: (OrchidCatalogItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var showSuggestions by remember { mutableStateOf(false) }

    val filteredItems = remember(searchQuery) {
        catalogItems.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

   LaunchedEffect(searchQuery) {
        showSuggestions = searchQuery.isNotBlank() && filteredItems.isNotEmpty()
        if (showSuggestions) keyboardController?.show()
    }

    Box(modifier = modifier) {
        Column {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onQueryChange(it) },
                label = { Text("Nome dal catalogo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) keyboardController?.show()
                    },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true
            )

            if (showSuggestions && filteredItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    LazyColumn {
                        items(filteredItems) { item ->
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item)
                                        onQueryChange(item.name)
                                        showSuggestions = false
                                        focusManager.clearFocus()
                                    }
                                    .padding(16.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}


@Composable
fun OrchidPhotoManager(
    imagePaths: List<String>,
    tempImageUris: List<Uri>,
    onImagesChanged: (List<String>) -> Unit,
    onPickImagesClicked: () -> Unit,
    maxImages: Int = 5
) {
    Column {
        Text("Foto", style = MaterialTheme.typography.titleMedium)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {

            items(imagePaths.size) { index ->
                Box(modifier = Modifier.size(100.dp)) {
                    LoadImageFromPath(imagePaths[index])
                    IconButton(
                        onClick = {
                            val updated = imagePaths.toMutableList().apply { removeAt(index) }
                            onImagesChanged(updated)
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        if (imagePaths.size + tempImageUris.size < maxImages) {
            Button(onClick = onPickImagesClicked) {
                Text("Aggiungi la foto")
            }
        } else {
            Text("Limite di $maxImages foto raggiunto", color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun LoadImageFromPath(path: String) {
    val bitmap = remember(path) {
        BitmapFactory.decodeFile(path)
    }
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

fun saveCompressedImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val filename = "orchid_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)

        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
        }

        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

@Composable
fun DateField(
    label: String,
    date: LocalDate?,
    onClick: () -> Unit
) {
    val text = date?.toString() ?: ""
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = text,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = true,
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onClick()
            }
        }
    }
}


@Composable
fun rememberDatePickerLauncher(): (onDateSelected: (LocalDate) -> Unit) -> Unit {
    val context = LocalContext.current
    val activity = context as? Activity

    return remember {
        { onDateSelected ->
            val today = LocalDate.now()
            activity?.let {
                android.app.DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onDateSelected(LocalDate.of(year, month + 1, day))
                    },
                    today.year,
                    today.monthValue - 1,
                    today.dayOfMonth
                ).show()
            }
        }
    }
}
@Composable
fun DateTimePickerField(
    label: String,
    selectedDateTime: Long?, // millis
    onDateTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm") }

    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val pickedDateTime = pickedDate.atTime(hourOfDay, minute)
                    val millis = pickedDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    onDateTimeSelected(millis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val displayText = selectedDateTime?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    } ?: ""

    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = true,
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                datePickerDialog.show()
            }
        }
    }
}


