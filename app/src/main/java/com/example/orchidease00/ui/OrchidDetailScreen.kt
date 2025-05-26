package com.example.orchidease00.ui

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orchidease00.R
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrchidImagePager(imageUrls: List<String>) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { imageUrls.size }
    )
    val context = LocalContext.current
    val density = LocalDensity.current.density

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            // Blocca svipe se ridimensionato
            val canSwipe = scale == 1f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(canSwipe) {
                        if (!canSwipe) {
                            // Regime di ridimensionamento
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 4f)
                                offset += pan * (1f/scale) // corregge panorama per lo scalo
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale > 1f) 1f else 2f
                                offset = Offset.Zero
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrls[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y,
                            transformOrigin = TransformOrigin.Center,
                            cameraDistance = 8f * density
                        )
                )
            }
        }

        // Indicatori delle pagine
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imageUrls.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (isSelected) 10.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun OrchidDetailScreen(
    name: String,
    viewModel: OrchidDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(name) {
        viewModel.loadOrchidDetail(name)
    }

    when (uiState) {
        is OrchidDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is OrchidDetailUiState.Error -> {
            val message = (uiState as OrchidDetailUiState.Error).message
            Text("Errore: $message", color = Color.Red)
        }

        is OrchidDetailUiState.Success -> {
            val state = uiState as OrchidDetailUiState.Success
            Log.d("ImagesDebug", "Ссылки: ${state.images}")

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Text("Nome: ${state.orchid?.name ?: ""}", style = MaterialTheme.typography.titleLarge)
                    Text("Descrizione: ${state.orchid?.description ?: ""}", style = MaterialTheme.typography.bodyLarge)
                    Text("Cura: ${state.orchid?.care ?: ""}", style = MaterialTheme.typography.bodyMedium)
                    Text("Imagini:", style = MaterialTheme.typography.titleMedium)
                    OrchidImagePager(state.images)
                }
            }
        }
    }
}

