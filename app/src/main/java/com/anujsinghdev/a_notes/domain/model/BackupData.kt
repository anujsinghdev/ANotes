package com.anujsinghdev.a_notes.domain.model

import com.anujsinghdev.a_notes.data.local.entity.FolderEntity
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity

data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val folders: List<FolderEntity>,
    val notes: List<NoteEntity>
)