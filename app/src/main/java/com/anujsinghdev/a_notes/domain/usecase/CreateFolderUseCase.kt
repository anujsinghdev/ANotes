package com.anujsinghdev.a_notes.domain.usecase

import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(name: String) {
        if (name.isBlank()) return
        repository.insertFolder(Folder(name = name))
    }
}