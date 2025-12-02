package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Note? {
        return repository.getNoteById(id)
    }
}
