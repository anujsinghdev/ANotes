package com.anujsinghdev.a_notes.data.local.dao

import androidx.room.*
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // Update sort order: Pinned first, then by custom position
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, position ASC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 AND isDeleted = 0")
    fun getArchivedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    // New: Get notes by folder

    @Query("SELECT * FROM notes WHERE folderId = :folderId AND isDeleted = 0 AND isArchived = 0 ORDER BY position ASC")
    fun getNotesByFolder(folderId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    // NEW: Get absolutely everything for Export
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesSync(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long



    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') AND isDeleted = 0")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Update
    suspend fun updateNotes(notes: List<NoteEntity>) // Batch update
}