package com.anujsinghdev.a_notes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anujsinghdev.a_notes.data.local.dao.FolderDao
import com.anujsinghdev.a_notes.data.local.dao.NoteDao
import com.anujsinghdev.a_notes.data.local.entity.FolderEntity
import com.anujsinghdev.a_notes.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class, FolderEntity::class],
    version = 4, // <--- UPDATED TO 4
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
}