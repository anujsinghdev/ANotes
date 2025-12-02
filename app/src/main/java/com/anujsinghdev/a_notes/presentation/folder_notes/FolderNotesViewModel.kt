package com.anujsinghdev.a_notes.presentation.folder_notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import com.anujsinghdev.a_notes.domain.usecase.DeleteFolderUseCase // New Import
import com.anujsinghdev.a_notes.domain.usecase.GetNotesByFolderUseCase
import com.anujsinghdev.a_notes.presentation.notes_list.NotesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderNotesViewModel @Inject constructor(
    private val getNotesByFolderUseCase: GetNotesByFolderUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase, // Inject this
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val folderId: Long = checkNotNull(savedStateHandle["folderId"]).toString().toLong()
    private val folderName: String = checkNotNull(savedStateHandle["folderName"])

    private val _uiState = MutableStateFlow<NotesListUiState>(NotesListUiState.Loading)
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    val currentFolderName = folderName
    val currentFolder = Folder(id = folderId, name = folderName) // create object for deletion

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesByFolderUseCase(folderId).collectLatest { notes ->
                _uiState.value = NotesListUiState.Success(notes = notes)
            }
        }
    }

    fun onNoteMoved(fromIndex: Int, toIndex: Int, notes: List<Note>) {
        viewModelScope.launch {
            val mutableNotes = notes.toMutableList()
            val item = mutableNotes.removeAt(fromIndex)
            mutableNotes.add(toIndex, item)

            val updatedNotes = mutableNotes.mapIndexed { index, note ->
                note.copy(position = index)
            }
            _uiState.value = NotesListUiState.Success(notes = updatedNotes)
            repository.updateNotesOrder(updatedNotes)
        }
    }

    // New Delete Function
    fun deleteFolder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteFolderUseCase(currentFolder)
            onSuccess()
        }
    }
}