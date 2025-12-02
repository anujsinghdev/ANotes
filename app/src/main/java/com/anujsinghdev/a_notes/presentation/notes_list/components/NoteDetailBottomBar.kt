package com.anujsinghdev.a_notes.presentation.note_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFormat // Correct Import
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteDetailBottomBar(
    timestamp: Long,
    onColorClick: () -> Unit,
    onFormatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Text Format Icon (Fixed Import)
            IconButton(onClick = onFormatClick) {
                Icon(
                    imageVector = Icons.Default.TextFormat, // Use Default instead of AutoMirrored
                    contentDescription = "Text Format"
                )
            }

            // Center: Palette Icon
            IconButton(onClick = onColorClick) {
                Icon(Icons.Outlined.Palette, contentDescription = "Change Color")
            }

            // Right Side: Edited Time
            Text(
                text = "Edited ${formatEditedTime(timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatEditedTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}