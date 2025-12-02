package com.anujsinghdev.a_notes.presentation.folder_notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Import Delete Icon
import androidx.compose.material.icons.filled.Warning // Import Warning Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anujsinghdev.a_notes.presentation.notes_list.NotesListUiState
import com.anujsinghdev.a_notes.presentation.notes_list.components.NoteCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FolderNotesScreen(
    onNavigateBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    viewModel: FolderNotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) } // Dialog State

    // --- Delete Confirmation Dialog ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text(text = "Delete Folder?") },
            text = {
                Text("Are you sure you want to delete '${viewModel.currentFolderName}'? Notes inside will not be deleted but will be removed from this folder.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteFolder {
                            onNavigateBack() // Go back after deletion
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    // ----------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.currentFolderName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // Add Actions for Delete Button
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Folder",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is NotesListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NotesListUiState.Success -> {
                    if (state.notes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notes in this folder")
                        }
                    } else {
                        val notes = state.notes
                        val gridState = rememberLazyStaggeredGridState()

                        val reorderableState = rememberReorderableLazyStaggeredGridState(gridState) { from, to ->
                            viewModel.onNoteMoved(from.index, to.index, notes)
                        }

                        LazyVerticalStaggeredGrid(
                            state = gridState,
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalItemSpacing = 8.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(notes, key = { it.id }) { note ->
                                ReorderableItem(reorderableState, key = note.id) { isDragging ->

                                    val elevation = if (isDragging) 8.dp else 0.dp

                                    NoteCard(
                                        note = note,
                                        onClick = { onNoteClick(note.id) },
                                        modifier = Modifier
                                            .draggableHandle()
                                            .graphicsLayer {
                                                scaleX = if (isDragging) 1.05f else 1f
                                                scaleY = if (isDragging) 1.05f else 1f
                                            }
                                            .zIndex(if (isDragging) 1f else 0f)
                                    )
                                }
                            }
                        }
                    }
                }
                is NotesListUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}