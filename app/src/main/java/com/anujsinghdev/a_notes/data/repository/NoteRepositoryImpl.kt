package com.anujsinghdev.a_notes.data.repository

import com.anujsinghdev.a_notes.data.local.dao.FolderDao
import com.anujsinghdev.a_notes.data.local.dao.NoteDao
import com.anujsinghdev.a_notes.data.local.entity.FolderEntity
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity
import com.anujsinghdev.a_notes.data.mapper.toDomain
import com.anujsinghdev.a_notes.data.mapper.toEntity
import com.anujsinghdev.a_notes.domain.model.Folder
import com.anujsinghdev.a_notes.domain.model.Note
import com.anujsinghdev.a_notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val folderDao: FolderDao
) : NoteRepository {

    private fun mapNotesWithFolders(notesFlow: Flow<List<NoteEntity>>): Flow<List<Note>> {
        return combine(notesFlow, folderDao.getAllFolders()) { notes, folders ->
            notes.map { noteEntity ->
                val folder = folders.find { it.id == noteEntity.folderId }
                noteEntity.toDomain(folderName = folder?.name)
            }
        }
    }

    // --- Duplicate deleteFolder removed from here ---

    override fun getNotes(): Flow<List<Note>> {
        return mapNotesWithFolders(noteDao.getAllNotes())
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return mapNotesWithFolders(noteDao.getArchivedNotes())
    }

    override fun getDeletedNotes(): Flow<List<Note>> {
        return mapNotesWithFolders(noteDao.getDeletedNotes())
    }

    override fun getNotesByFolder(folderId: Long): Flow<List<Note>> {
        return mapNotesWithFolders(noteDao.getNotesByFolder(folderId))
    }

    override suspend fun getNoteById(id: Long): Note? {
        val noteEntity = noteDao.getNoteById(id) ?: return null
        return noteEntity.toDomain()
    }

    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deletePermanently(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    override suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return mapNotesWithFolders(noteDao.searchNotes(query))
    }

    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder.toEntity())
    }

    // This is the single correct implementation
    override suspend fun deleteFolder(folder: Folder) {
        folderDao.deleteFolder(folder.toEntity())
    }

    override suspend fun updateNotesOrder(notes: List<Note>) {
        val noteEntities = notes.map { it.toEntity() }
        noteDao.updateNotes(noteEntities)
    }

    override suspend fun getBackupData(): Pair<List<FolderEntity>, List<NoteEntity>> {
        return Pair(folderDao.getAllFoldersSync(), noteDao.getAllNotesSync())
    }

    override suspend fun restoreBackup(folders: List<FolderEntity>, notes: List<NoteEntity>) {
        val folderIdMap = mutableMapOf<Long, Long>()

        folders.forEach { oldFolder ->
            val newId = folderDao.insertFolder(oldFolder.copy(id = 0))
            folderIdMap[oldFolder.id] = newId
        }

        notes.forEach { note ->
            val newFolderId = if (note.folderId != null) folderIdMap[note.folderId] else null
            noteDao.insertNote(note.copy(id = 0, folderId = newFolderId))
        }
    }
}