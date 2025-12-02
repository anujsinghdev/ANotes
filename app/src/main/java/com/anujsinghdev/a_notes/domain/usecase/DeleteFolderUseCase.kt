package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(folder: Folder) {
        repository.deleteFolder(folder)
    }
}