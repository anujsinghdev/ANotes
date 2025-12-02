package com.anujsinghdev.a_notes.presentation.notes_list

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anujsinghdev.a_notes.domain.model.BackupData
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import com.anujsinghdev.a_notes.domain.usecase.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val reorderNotesUseCase: ReorderNotesUseCase,
    private val repository: NoteRepository // Added for Import/Export
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesListUiState>(NotesListUiState.Loading)
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Folders State
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    init {
        loadNotes()
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            getFoldersUseCase().collectLatest {
                _folders.value = it
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            createFolderUseCase(name)
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            try {
                combine(
                    getNotesUseCase(),
                    _searchQuery
                ) { notes, query ->
                    if (query.isBlank()) {
                        notes // Returns sorted by position from Repository
                    } else {
                        searchNotesUseCase(query).first()
                    }
                }.collect { notes ->
                    _uiState.value = NotesListUiState.Success(
                        notes = notes,
                        isSearchActive = _searchQuery.value.isNotBlank(),
                        searchQuery = _searchQuery.value
                    )
                }
            } catch (e: Exception) {
                _uiState.value = NotesListUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Handle reordering
    fun onNoteMoved(fromIndex: Int, toIndex: Int, currentNotes: List<Note>) {
        if (_searchQuery.value.isNotBlank()) return

        viewModelScope.launch {
            val mutableNotes = currentNotes.toMutableList()
            val item = mutableNotes.removeAt(fromIndex)
            mutableNotes.add(toIndex, item)

            val updatedNotes = mutableNotes.mapIndexed { index, note ->
                note.copy(position = index)
            }

            val currentState = _uiState.value
            if (currentState is NotesListUiState.Success) {
                _uiState.value = currentState.copy(notes = updatedNotes)
            }

            reorderNotesUseCase(updatedNotes)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note)
        }
    }

    fun pinNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase(note.copy(isPinned = !note.isPinned))
        }
    }

    fun archiveNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase(note.copy(isArchived = true))
        }
    }

    // --- Import / Export Logic (Step 5) ---

    fun exportNotes(uri: Uri, context: Context, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Get All Data
                val (folders, notes) = repository.getBackupData()
                val backup = BackupData(folders = folders, notes = notes)

                // 2. Convert to JSON
                val jsonString = Gson().toJson(backup)

                // 3. Write to selected file URI
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }

                withContext(Dispatchers.Main) { onResult("Export Successful!") }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onResult("Export Failed: ${e.message}") }
            }
        }
    }

    fun importNotes(uri: Uri, context: Context, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Read JSON from File URI
                val stringBuilder = StringBuilder()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                    }
                }

                // 2. Parse JSON
                val jsonString = stringBuilder.toString()
                val backup = Gson().fromJson(jsonString, BackupData::class.java)

                // 3. Restore Data to DB
                if (backup.notes.isNotEmpty() || backup.folders.isNotEmpty()) {
                    repository.restoreBackup(backup.folders, backup.notes)
                    withContext(Dispatchers.Main) { onResult("Import Successful!") }
                } else {
                    withContext(Dispatchers.Main) { onResult("No data found in file.") }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { onResult("Import Failed: ${e.message}") }
            }
        }
    }
}