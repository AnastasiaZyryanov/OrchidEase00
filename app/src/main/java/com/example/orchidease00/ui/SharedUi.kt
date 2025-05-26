package com.example.orchidease00.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.LocalDate
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap

import java.util.*
import com.example.orchidease00.data.OrchidCatalogItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun CatalogNameAutocompleteField(
    catalogItems: List<OrchidCatalogItem>,
    selectedItem: OrchidCatalogItem?,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onItemSelected: (OrchidCatalogItem) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            onQueryChange(it)
            expanded = true
        },
        label = { Text("Nome dal catalogo") },
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )

    DropdownMenu(
        expanded = expanded && searchQuery.isNotBlank(),
        onDismissRequest = { expanded = false }
    ) {
        catalogItems
            .filter { it.name.contains(searchQuery, ignoreCase = true) }
            .forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onItemSelected(item)
                        onQueryChange(item.name)
                        expanded = false
                    }
                )
            }
    }
}
/*
@Composable
fun OrchidPhotoManager(
    imageUris: List<Uri>,
    onImagesChanged: (List<Uri>) -> Unit,
    onPickImagesClicked: () -> Unit
) {
    Column {
        Text("Foto", style = MaterialTheme.typography.titleMedium)

        LazyRow {
            items(imageUris) { uri ->
                Box(modifier = Modifier.padding(4.dp)) {
                    SubcomposeAsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Errore di caricamento",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    )

                }

                    IconButton(
                        onClick = {
                            val updatedList = imageUris.toMutableList().apply {
                                remove(uri)
                            }
                            onImagesChanged(updatedList)
                        },
                        modifier = Modifier
                            //.align(Alignment.Center)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Cancellare", tint = Color.Red)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onPickImagesClicked) {
            Text("Aggiungi la foto")
        }
}
 */

@Composable
fun OrchidPhotoManager(
    //imageUris: List<Uri>,
    imagePath: String,
    tempImageUri: Uri?,
    onImagesChanged: (String) -> Unit,
    //onImagesChanged: (List<Uri>) -> Unit,
    onPickImagesClicked: () -> Unit
) {
    Column {
        Text("Foto", style = MaterialTheme.typography.titleMedium)

        /*
        LazyRow {
            items(imageUris) { uri ->
                Box(modifier = Modifier.padding(4.dp)) {
                    val context = LocalContext.current
                    val bitmap = remember(uri) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                        } catch (e: Exception) {
                            Log.e("ImageLoad", "Error loading image", e)
                            null
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            painter = BitmapPainter(bitmap),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Gray)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Error")
                        }
                    }
                    IconButton(
                        onClick = { onImagesChanged(imageUris - uri) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
         */
        if (imagePath.isNotEmpty()) {
            val bitmap = rememberBitmapFromPath(imagePath)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Text("Non riuscito caricare l'imagine")
            }
        }

        if (imagePath.isNotEmpty() || tempImageUri != null) {
            Box(modifier = Modifier.padding(4.dp)) {
                if (imagePath.isNotEmpty()) {
                    LoadImageFromPath(imagePath)
                } else {
                    tempImageUri?.let { uri ->
                        LoadImageFromUri(uri)
                    }
                }

                IconButton(
                    onClick = { onImagesChanged("") },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onPickImagesClicked) {
            Text("Aggiungi la foto")
        }
    }
}
// Новая функция для безопасной загрузки
@Composable
fun rememberBitmapFromPath(path: String?): ImageBitmap? {
    return remember(path) {
        path?.let {
            try {
                BitmapFactory.decodeFile(it)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
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

@Composable
fun LoadImageFromUri(uri: Uri) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }
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
            // Comprensione fino a JPEG 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
        }

        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "orchid_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Composable
fun DateField(label: String, value: LocalDate?, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() }  //
    ) {
        OutlinedTextField(
            value = value?.toString() ?: "",
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false  // per non aprire la tastiera
        )
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
