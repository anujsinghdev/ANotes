package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotes().map { notes ->
            notes
                .filter { !it.isDeleted && !it.isArchived }
                .sortedWith(
                    compareByDescending<Note> { it.isPinned }
                        .thenByDescending { it.timestamp }
                )
        }
    }
}
