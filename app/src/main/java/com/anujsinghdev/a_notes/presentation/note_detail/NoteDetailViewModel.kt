package com.anujsinghdev.a_notes.presentation.note_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val insertNoteUseCase: InsertNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getFoldersUseCase: GetFoldersUseCase, // New
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.get<String>("noteId")?.toLongOrNull()

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    // Folders list for the dialog
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    init {
        noteId?.let { loadNote(it) }
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            getFoldersUseCase().collectLatest {
                _folders.value = it
            }
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val note = getNoteByIdUseCase(id)
            note?.let {
                _uiState.value = _uiState.value.copy(
                    note = it,
                    title = it.title,
                    content = it.content,
                    color = it.color,
                    textSize = it.textSize,
                    isPinned = it.isPinned,
                    isLoading = false
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onContentChange(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun onColorChange(color: Int) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    fun onTextSizeChange(size: Int) {
        _uiState.value = _uiState.value.copy(textSize = size)
    }

    fun togglePin() {
        _uiState.value = _uiState.value.copy(isPinned = !_uiState.value.isPinned)
    }

    fun moveNoteToFolder(folderId: Long) {
        viewModelScope.launch {
            // Update the state so it's reflected if we save
            // But also perform immediate update if note exists
            val currentState = _uiState.value
            // We can't update uiState.note.folderId directly as it's part of the Note object which is immutable in the state mostly
            // But we should update the DB.

            if (noteId != null) {
                // Fetch current note fresh or use state
                val note = getNoteByIdUseCase(noteId)
                note?.let {
                    updateNoteUseCase(it.copy(folderId = folderId))
                }
            } else {
                // New note: We need to store this folderId selection so when saveNote is called, it uses it.
                // Currently NoteDetailUiState doesn't have folderId. Let's assume we save it immediately if it's a new note?
                // Or better, add folderId to UI State.
                // For simplicity, I'll add folderId to UI State.
            }
        }
    }

    // Simpler approach for Move To Folder:
    // Just update the note in DB.
    // If it's a new note, we can't move it yet effectively unless we save it.
    // Let's assume we save it first.
    fun moveNoteToFolder(folder: Folder, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val note = Note(
                id = noteId ?: 0,
                title = state.title,
                content = state.content,
                color = state.color,
                textSize = state.textSize,
                isPinned = state.isPinned,
                isArchived = state.note?.isArchived ?: false,
                isDeleted = false,
                timestamp = System.currentTimeMillis(),
                folderId = folder.id // SET FOLDER ID
            )

            if (noteId == null) {
                val newId = insertNoteUseCase(note)
                // Update ID for future edits
            } else {
                updateNoteUseCase(note)
            }
            _uiState.value = state.copy(isSaved = true)
            onSuccess()
        }
    }

    fun archiveNote(onSuccess: (String) -> Unit) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isSaved = true)
        onSuccess(currentState.title)

        viewModelScope.launch(Dispatchers.IO) {
            val note = Note(
                id = noteId ?: 0,
                title = currentState.title,
                content = currentState.content,
                color = currentState.color,
                textSize = currentState.textSize,
                isPinned = currentState.isPinned,
                isArchived = true,
                isDeleted = false,
                timestamp = System.currentTimeMillis(),
                folderId = currentState.note?.folderId // Preserve folder
            )

            if (noteId == null) {
                insertNoteUseCase(note)
            } else {
                updateNoteUseCase(note)
            }
        }
    }

    fun deleteNote(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isSaved = true)
        onSuccess()

        viewModelScope.launch(Dispatchers.IO) {
            if (noteId != null) {
                val noteToDelete = Note(
                    id = noteId,
                    title = currentState.title,
                    content = currentState.content,
                    color = currentState.color,
                    textSize = currentState.textSize,
                    isPinned = currentState.isPinned,
                    isArchived = currentState.note?.isArchived ?: false,
                    isDeleted = true,
                    timestamp = System.currentTimeMillis(),
                    folderId = currentState.note?.folderId // Preserve folder
                )
                deleteNoteUseCase(noteToDelete)
            }
        }
    }

    fun copyNote(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val newNote = Note(
                id = 0,
                title = state.title,
                content = state.content,
                color = state.color,
                textSize = state.textSize,
                isPinned = false,
                isArchived = false,
                isDeleted = false,
                timestamp = System.currentTimeMillis(),
                folderId = state.note?.folderId // Copy folder too? Usually yes.
            )
            insertNoteUseCase(newNote)
            onSuccess("Note copied successfully")
        }
    }

    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.title.isBlank() && state.content.isBlank()) {
                onSaved()
                return@launch
            }

            if (state.isSaved) {
                onSaved()
                return@launch
            }

            val note = Note(
                id = noteId ?: 0,
                title = state.title,
                content = state.content,
                color = state.color,
                textSize = state.textSize,
                isPinned = state.isPinned,
                isArchived = state.note?.isArchived ?: false,
                timestamp = System.currentTimeMillis(),
                folderId = state.note?.folderId // Preserve folder
            )

            if (noteId == null) {
                insertNoteUseCase(note)
            } else {
                updateNoteUseCase(note)
            }

            _uiState.value = state.copy(isSaved = true)
            onSaved()
        }
    }
}