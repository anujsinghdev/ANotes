package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import javax.inject.Inject

class PinNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note.copy(isPinned = !note.isPinned))
    }
}
