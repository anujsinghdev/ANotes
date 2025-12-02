package com.anujsinghdev.a_notes.presentation.archive_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import com.anujsinghdev.a_notes.presentation.notes_list.NotesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveNotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesListUiState>(NotesListUiState.Loading)
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    init {
        getArchivedNotes()
    }

    private fun getArchivedNotes() {
        viewModelScope.launch {
            repository.getArchivedNotes().collectLatest { notes ->
                _uiState.value = NotesListUiState.Success(notes = notes)
            }
        }
    }
}