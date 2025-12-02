package com.anujsinghdev.a_notes.data.mapper

import com.anujsinghdev.a_notes.data.local.entity.FolderEntity
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note

fun NoteEntity.toDomain(folderName: String? = null): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        color = color,
        textSize = textSize,
        timestamp = timestamp,
        isPinned = isPinned,
        isArchived = isArchived,
        isDeleted = isDeleted,
        folderId = folderId,
        folderName = folderName,
        position = position // Map position
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        color = color,
        textSize = textSize,
        timestamp = timestamp,
        isPinned = isPinned,
        isArchived = isArchived,
        isDeleted = isDeleted,
        folderId = folderId,
        position = position // Map position
    )
}

fun FolderEntity.toDomain(): Folder {
    return Folder(
        id = id,
        name = name
    )
}

fun Folder.toEntity(): FolderEntity {
    return FolderEntity(
        id = id,
        name = name
    )
}