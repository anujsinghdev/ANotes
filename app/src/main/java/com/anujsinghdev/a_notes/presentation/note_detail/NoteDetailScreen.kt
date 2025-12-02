package com.anujsinghdev.a_notes.presentation.note_detail

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.presentation.note_detail.components.NoteDetailBottomBar
import com.anujsinghdev.a_notes.presentation.theme.NoteColorDefault
import com.anujsinghdev.a_notes.presentation.theme.noteColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle() // Get folders

    val context = LocalContext.current

    // Toggle States
    var showColorPicker by remember { mutableStateOf(false) }
    var showFormatOptions by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) } // State for folder dialog

    // Snackbar State
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isDefaultColor = uiState.color == NoteColorDefault.toArgb()

    val backgroundColor = if (isDefaultColor) {
        MaterialTheme.colorScheme.background
    } else {
        Color(uiState.color)
    }

    val contentColor = if (isDefaultColor) {
        MaterialTheme.colorScheme.onBackground
    } else {
        Color.Black.copy(alpha = 0.8f)
    }

    BackHandler {
        viewModel.saveNote(onNavigateBack)
    }

    // Folder Selection Dialog
    if (showFolderDialog) {
        AlertDialog(
            onDismissRequest = { showFolderDialog = false },
            title = { Text("Select Label") },
            text = {
                Column {
                    if (folders.isEmpty()) {
                        Text("No labels created yet. Create one from the main menu.")
                    } else {
                        folders.forEach { folder ->
                            TextButton(
                                onClick = {
                                    viewModel.moveNoteToFolder(folder) {
                                        showFolderDialog = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Moved to ${folder.name}")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(folder.name, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFolderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.saveNote(onNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = contentColor
                        )
                    }
                },
                actions = {
                    // Pin Icon
                    IconButton(onClick = { viewModel.togglePin() }) {
                        Icon(
                            imageVector = if (uiState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (uiState.isPinned) "Unpin" else "Pin",
                            tint = contentColor
                        )
                    }

                    // Archive Icon
                    IconButton(onClick = {
                        viewModel.archiveNote { title ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Note archived")
                                onNavigateBack()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Archive, "Archive", tint = contentColor)
                    }

                    // Menu Icon & Dropdown
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "More options", tint = contentColor)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = 4.dp
                        ) {
                            // 1. Delete
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.deleteNote {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Note deleted")
                                            onNavigateBack()
                                        }
                                    }
                                }
                            )

                            // 2. Make a copy
                            DropdownMenuItem(
                                text = { Text("Make a copy") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.copyNote { msg ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                }
                            )

                            // 3. Send (Share)
                            DropdownMenuItem(
                                text = { Text("Send") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "${uiState.title}\n\n${uiState.content}")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            )

                            // 4. Folder
                            DropdownMenuItem(
                                text = { Text("Folder") },
                                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    showFolderDialog = true // Show dialog
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                NoteDetailBottomBar(
                    timestamp = System.currentTimeMillis(),
                    onColorClick = {
                        showColorPicker = !showColorPicker
                        showFormatOptions = false
                    },
                    onFormatClick = {
                        showFormatOptions = !showFormatOptions
                        showColorPicker = false
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            TextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = {
                    Text("Title", style = MaterialTheme.typography.headlineMedium.copy(color = contentColor.copy(alpha = 0.5f)))
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = contentColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = contentColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = uiState.content,
                onValueChange = viewModel::onContentChange,
                placeholder = {
                    Text(
                        "Note",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = contentColor.copy(alpha = 0.5f),
                            fontSize = uiState.textSize.sp
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = contentColor,
                    fontSize = uiState.textSize.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = contentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (showColorPicker) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    items(noteColors) { color ->
                        val isSelected = color.toArgb() == uiState.color
                        val isThisColorDefault = color == NoteColorDefault
                        val bubbleColor = if (isThisColorDefault) MaterialTheme.colorScheme.surface else color

                        ColorCircle(
                            color = bubbleColor,
                            isSelected = isSelected,
                            hasBorder = isThisColorDefault,
                            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            onClick = { viewModel.onColorChange(color.toArgb()) }
                        )
                    }
                }
            }

            if (showFormatOptions) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FormatButton(
                        text = "H1",
                        isSelected = uiState.textSize == 28,
                        contentColor = contentColor,
                        onClick = { viewModel.onTextSizeChange(28) }
                    )
                    FormatButton(
                        text = "H2",
                        isSelected = uiState.textSize == 22,
                        contentColor = contentColor,
                        onClick = { viewModel.onTextSizeChange(22) }
                    )
                    FormatButton(
                        text = "Aa",
                        isSelected = uiState.textSize == 16,
                        contentColor = contentColor,
                        onClick = { viewModel.onTextSizeChange(16) }
                    )
                }
            }
        }
    }
}

// Helper Components... (ColorCircle and FormatButton remain the same)
@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    hasBorder: Boolean,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color)
            .then(if (hasBorder) Modifier.border(1.dp, borderColor, CircleShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (hasBorder) MaterialTheme.colorScheme.onSurface else Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FormatButton(
    text: String,
    isSelected: Boolean,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) contentColor.copy(alpha = 0.15f) else Color.Transparent,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f)),
        modifier = Modifier.height(48.dp).width(60.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) contentColor else contentColor.copy(alpha = 0.7f)
            )
        }
    }
}