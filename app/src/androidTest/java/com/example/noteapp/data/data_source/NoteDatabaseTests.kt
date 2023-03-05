package com.example.noteapp.data.data_source

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.noteapp.data.model.Note
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTests {
    private lateinit var notesDao: NotesDao
    private lateinit var db: NotesDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        notesDao = db.notesDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeReadNote() {
        runBlocking {
            val note = Note(noteTitle = "Test Title", noteContent = "TestContent")
            notesDao.insertNote(note)
            val dateCreated = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val expectedNote =
                Note(noteId = 1, noteTitle = "Test Title", noteContent = "TestContent", dateCreated = dateCreated)
            val resultNote = notesDao.getNoteById(1)
            assertEquals(expectedNote, resultNote)
        }
    }


    @Test
    @Throws(Exception::class)
    fun getNotesByRedSearchQuery(){
        runBlocking {
            val notesList = listOf(
                Note(noteTitle = "Red title", noteContent = "Just test note"),
                Note(noteTitle = "Test title", noteContent = "Content that has red word"),
                Note(noteTitle = "Red finished note", noteContent = "Random content", isPinned = true),
                Note(noteTitle = "My thoughts", noteContent = "Random old red note", isPinned = true, dateCreated = "04.03.2023"),
                Note(noteTitle = "Title", noteContent = "Content")
            )
            notesList.forEach {note ->
                notesDao.insertNote(note)
            }
            val resultNotesList = notesDao.getNotesBySearchQuery("red").first()
            val expectedNotesList = listOf(
                Note(noteTitle = "Red finished note", noteContent = "Random content", isPinned = true),
                Note(noteTitle = "My thoughts", noteContent = "Random old red note", isPinned = true, dateCreated = "04.03.2023"),
                Note(noteTitle = "Red title", noteContent = "Just test note"),
                Note(noteTitle = "Test title", noteContent = "Content that has red word")
            )
            assertEquals(expectedNotesList.map { note -> note.copy(noteId = 0) }, resultNotesList.map { note -> note.copy(noteId = 0) })
        }
    }

    @Test
    @Throws(Exception::class)
    fun getNotesByEmptySearchQuery(){
        runBlocking {
            val notesList = listOf(
                Note(noteTitle = "First note", noteContent = "Test content", isPinned = true),
                Note(noteTitle = "Second note", noteContent = "Test content", isPinned = false)
            )
            notesList.forEach{note ->
                notesDao.insertNote(note)
            }
            val resultList = notesDao.getNotesBySearchQuery("").first()
            assertEquals(notesList.map{note -> note.copy(noteId = 0)}, resultList.map{note -> note.copy(noteId = 0)})
        }
    }

    @Test
    @Throws(Exception::class)
    fun emptyNotesWithNoAppropriateSearchQuery(){
        runBlocking {
            val notesList = listOf(
                Note(noteTitle = "First note", noteContent = "Test content", isPinned = true),
                Note(noteTitle = "Second note", noteContent = "Test content", isPinned = false)
            )
            notesList.forEach{note ->
                notesDao.insertNote(note)
            }
            val resultList = notesDao.getNotesBySearchQuery("abcb").first()
            assertEquals(emptyList<Note>(), resultList)
        }
    }
}