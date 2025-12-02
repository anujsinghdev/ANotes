package com.anujsinghdev.a_notes.presentation.note_detail

import com.anujsinghdev.a_notes.domain.model.Note

data class NoteDetailUiState(
    val note: Note? = null,
    val title: String = "",
    val content: String = "",
    val color: Int = 0xFFFFFFFF.toInt(),
    val textSize: Int = 16, // Default to 16sp (Regular)
    val isPinned: Boolean = false, // Added isPinned
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)