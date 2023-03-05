package com.example.noteapp.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM note WHERE noteId = :noteId")
    suspend fun getNoteById(noteId: Long): Note

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note WHERE noteTitle LIKE '%' || :searchQuery || '%' OR" +
            " noteContent LIKE '%' || :searchQuery || '%' ORDER BY isPinned DESC, dateCreated DESC")
    fun getNotesBySearchQuery(searchQuery: String): Flow<List<Note>>
}