package com.anujsinghdev.a_notes.presentation.notes_list

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult // Added
import androidx.activity.result.contract.ActivityResultContracts // Added
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.presentation.notes_list.components.CreateFolderDialog
import com.anujsinghdev.a_notes.presentation.notes_list.components.EmptyNotesState
import com.anujsinghdev.a_notes.presentation.notes_list.components.KeepStyleSearchBar
import com.anujsinghdev.a_notes.presentation.notes_list.components.NoteCard
import com.anujsinghdev.a_notes.presentation.notes_list.components.NotesDrawerSheet
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToDeleted: () -> Unit,
    onNavigateToFolder: (Folder) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    viewModel: NotesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()

    var isSearchActive by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }

    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val uriHandler = LocalUriHandler.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // State for Snackbars

    // --- Import/Export Launchers ---
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.exportNotes(it, context) { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.importNotes(it, context) { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }
    // -------------------------------

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            searchFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    BackHandler(enabled = isSearchActive || drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            isSearchActive = false
            viewModel.onSearchQueryChanged("")
        }
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onCreate = { name -> viewModel.createFolder(name) }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NotesDrawerSheet(
                folders = folders,
                onItemClick = { itemLabel ->
                    scope.launch { drawerState.close() }
                    when (itemLabel) {
                        "Archive" -> onNavigateToArchive()
                        "Deleted" -> onNavigateToDeleted()
                        // --- Handle Import/Export Clicks ---
                        "Import" -> {
                            importLauncher.launch("application/json")
                        }
                        "Export" -> {
                            val fileName = "anotes_backup_${System.currentTimeMillis()}.json"
                            exportLauncher.launch(fileName)
                        }
                        // -----------------------------------
                        "Terms" -> uriHandler.openUri("https://doc-hosting.flycricket.io/a-notes-terms-of-use/96e094ec-c63e-4791-a2e8-f40e61fee5b4/terms")
                        "Privacy" -> uriHandler.openUri("https://doc-hosting.flycricket.io/a-notes-privacy-policy/7e13dff0-2e15-46f1-b99b-e3ae0cd78b53/privacy")
                        "Contact" -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:anuj1112131415@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "A Notes Feedback")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) { }
                        }
                    }
                },
                onFolderClick = { folder ->
                    scope.launch { drawerState.close() }
                    onNavigateToFolder(folder)
                },
                onCreateFolderClick = {
                    scope.launch { drawerState.close() }
                    showCreateFolderDialog = true
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost here
            topBar = {
                if (isSearchActive) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onSearch = { focusManager.clearFocus() },
                        active = true,
                        onActiveChange = {
                            if (!it) {
                                isSearchActive = false
                                viewModel.onSearchQueryChanged("")
                            }
                        },
                        placeholder = { Text("Search A Notes") },
                        leadingIcon = {
                            IconButton(onClick = {
                                isSearchActive = false
                                viewModel.onSearchQueryChanged("")
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(searchFocusRequester)
                    ) {
                        // Search Results Grid (Usually not reorderable)
                        when (val state = uiState) {
                            is NotesListUiState.Success -> {
                                if (searchQuery.isNotEmpty()) {
                                    if (state.notes.isNotEmpty()) {
                                        LazyVerticalStaggeredGrid(
                                            columns = StaggeredGridCells.Fixed(2),
                                            contentPadding = PaddingValues(16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalItemSpacing = 8.dp,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(state.notes, key = { it.id }) { note ->
                                                NoteCard(note = note, onClick = { onNoteClick(note.id) })
                                            }
                                        }
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text("No matching notes")
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                } else {
                    KeepStyleSearchBar(
                        query = searchQuery,
                        onSearchClick = { isSearchActive = true },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle
                    )
                }
            },
            floatingActionButton = {
                if (!isSearchActive) {
                    FloatingActionButton(
                        onClick = onCreateNote,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create note")
                    }
                }
            }
        ) { paddingValues ->
            if (!isSearchActive) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when (val state = uiState) {
                        is NotesListUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is NotesListUiState.Success -> {
                            if (state.notes.isEmpty()) {
                                EmptyNotesState()
                            } else {
                                // --- Reorderable Grid Implementation Start ---
                                val notes = state.notes
                                val gridState = rememberLazyStaggeredGridState()

                                val reorderableState = rememberReorderableLazyStaggeredGridState(gridState) { from, to ->
                                    // 1. Update the list in ViewModel
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

                                            // Optional: Add a slight elevation or scale effect when dragging
                                            val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")
                                            val scale by animateFloatAsState(if (isDragging) 1.05f else 1f, label = "scale")

                                            Box(
                                                modifier = Modifier
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                    }
                                                    // FIX: Use .zIndex() function, not assignment
                                                    .zIndex(if (isDragging) 1f else 0f)
                                                    // Use longPressDraggableHandle to allow normal clicks AND drag
                                                    .longPressDraggableHandle()
                                            ) {
                                                NoteCard(
                                                    note = note,
                                                    onClick = {
                                                        // Prevent click if dragging just finished
                                                        if (!isDragging) onNoteClick(note.id)
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
                                // --- Reorderable Grid Implementation End ---
                            }
                        }
                        is NotesListUiState.Error -> { /* Error handling */ }
                    }
                }
            }
        }
    }
}