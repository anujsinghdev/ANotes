package com.anujsinghdev.a_notes.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val color: Int,
    val textSize: Int = 16,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val folderId: Long? = null,
    val folderName: String? = null,
    val position: Int = 0 // Added field
)