package com.anujsinghdev.a_notes.domain.repository

import com.anujsinghdev.a_notes.data.local.entity.FolderEntity
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>
    fun getDeletedNotes(): Flow<List<Note>>
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deletePermanently(note: Note)
    suspend fun emptyTrash()
    fun searchNotes(query: String): Flow<List<Note>>

    // Folder methods
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun insertFolder(folder: Folder)

    // FIX: Changed from 'folderId: Long' to 'folder: Folder' to match Implementation
    suspend fun deleteFolder(folder: Folder)

    suspend fun updateNotesOrder(notes: List<Note>)

    // Backup Methods
    suspend fun getBackupData(): Pair<List<FolderEntity>, List<NoteEntity>>
    suspend fun restoreBackup(folders: List<FolderEntity>, notes: List<NoteEntity>)
}