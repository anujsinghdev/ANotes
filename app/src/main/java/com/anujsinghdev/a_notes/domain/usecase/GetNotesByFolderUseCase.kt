package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesByFolderUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(folderId: Long): Flow<List<Note>> {
        return repository.getNotesByFolder(folderId)
    }
}