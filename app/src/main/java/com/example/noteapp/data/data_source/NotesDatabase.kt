package com.example.noteapp.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noteapp.data.model.Note

@Database(
    entities =
        [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract val notesDao: NotesDao

    companion object {
        const val DATABASE_NAME = "notes_db"
    }
}