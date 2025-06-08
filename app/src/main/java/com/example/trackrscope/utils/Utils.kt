package com.example.trackrscope.utils

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

/**
 * Componente para mostrar texto HTML.
 *
 * @param html Texto HTML a mostrar.
 * @param modifier Modificador para personalizar el componente.
 * @param textColor Color del texto.
 */
@Composable
fun HtmlTextView(
    html: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                if (textColor != Color.Unspecified) setTextColor(textColor.toArgb())
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (textColor != Color.Unspecified) textView.setTextColor(textColor.toArgb())
        }
    )
}