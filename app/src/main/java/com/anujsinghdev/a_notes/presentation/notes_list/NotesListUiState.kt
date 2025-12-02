package com.anujsinghdev.a_notes.presentation.notes_list

import com.anujsinghdev.a_notes.domain.model.Note

sealed interface NotesListUiState {
    data object Loading : NotesListUiState
    data class Success(
        val notes: List<Note>,
        val isSearchActive: Boolean = false,
        val searchQuery: String = ""
    ) : NotesListUiState
    data class Error(val message: String) : NotesListUiState
}
