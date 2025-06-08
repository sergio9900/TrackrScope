package com.example.trackrscope.screens.news

import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.trackrscope.utils.LoadingIndicator
import io.noties.markwon.Markwon

/**
 * Composable que muestra las actualizaciones de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen() {

    val context = LocalContext.current
    var listNews by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf<Pair<String, String>?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val archivo = "TrackrScope_Changelog.md"

    LaunchedEffect(archivo) {
        try {
            val content = context.assets.open(archivo).bufferedReader().use { it.readText() }

            listNews = content.split("## ").filter { it.isNotBlank() }.map {
                val lines = it.lines()
                val title = lines.firstOrNull() ?: "No tiene título"
                val body = lines.drop(1).joinToString("\n")
                title to body.trim()
            }


        } catch (e: Exception) {
            Log.e("NewsScreen", "Error al cargar el archivo: $e")
            listNews = listOf("Error" to "No se pudo cargar el archivo")
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        NewsList(listNews) { selectedNews ->
            selected = selectedNews
        }

        selected?.let { news ->
            ModalBottomSheet(
                onDismissRequest = { selected = null },
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                dragHandle = null,
            ) {
                NewsBottomSheetContent(
                    title = news.first,
                    content = news.second
                )
            }
        }
    }
}

/**
 * Composable que muestra el contenido del ModalBottomSheet.
 *
 * @param title Título del changelog.
 * @param content Contenido del changelog.
 */
@Composable
fun NewsBottomSheetContent(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        // Título del changelog
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Contenido scrolleable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            MarkDownText(content)
        }
    }
}

/**
 * Composable que muestra el contenido del changelog en formato Markdown.
 *
 * @param markdownContent Contenido del changelog en formato Markdown.
 */
@Composable
fun MarkDownText(markdownContent: String) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {

        AndroidView(
            factory = { TextView(it) },
            update = { textView ->
                markwon.setMarkdown(textView, markdownContent)
            }
        )
    }
}

/**
 * Composable que muestra la lista de actualizaciones.
 *
 * @param newsList Lista de actualizaciones.
 * @param onNewsClick Función a ejecutar al hacer clic en una actualización.
 */
@Composable
fun NewsList(newsList: List<Pair<String, String>>, onNewsClick: (Pair<String, String>) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(newsList.size) { index ->
            val (title, body) = newsList[index]
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxSize(),
                onClick = { onNewsClick(title to body) },
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(title)
                }
            }
        }
    }
}