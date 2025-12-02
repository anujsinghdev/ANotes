package com.anujsinghdev.a_notes.presentation.deleted_notes

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
class DeletedNotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesListUiState>(NotesListUiState.Loading)
    val uiState: StateFlow<NotesListUiState> = _uiState.asStateFlow()

    init {
        getDeletedNotes()
    }

    private fun getDeletedNotes() {
        viewModelScope.launch {
            repository.getDeletedNotes().collectLatest { notes ->
                _uiState.value = NotesListUiState.Success(notes = notes)
            }
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
        }
    }
}