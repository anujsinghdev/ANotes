package com.anujsinghdev.a_notes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val color: Int,
    val textSize: Int = 16,
    val timestamp: Long,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isDeleted: Boolean,
    val folderId: Long? = null, // New field: Link to FolderEntity
    val position: Int = 0 // New field for reordering
)