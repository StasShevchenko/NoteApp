package com.example.noteapp.presentation.notes_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.data_source.NotesDao
import com.example.noteapp.data.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesScreenViewModel @Inject constructor(
    private val dao: NotesDao
) : ViewModel() {

    private var deletedNote: Note? = null
    fun deleteNote(deletedNoteId: Long) {
        viewModelScope.launch {
            dao.deleteNoteById(deletedNoteId)
        }
    }

    fun searchNotes(searchQuery: String){
        job?.cancel()
        job = viewModelScope.launch {
            dao.getNotesBySearchQuery(searchQuery).collectLatest { notes ->
                _notes.value = notes
            }
        }
    }

    fun restoreNote() {
        val restoredNoteList = _notes.value.toMutableList()
        restoredNoteList.add(deletedNote!!)
        _notes.value = restoredNoteList
    }

    fun hideNote(deletedNoteId: Long){
        val deletedNoteList = _notes.value.toMutableList()
        deletedNote = deletedNoteList.find {
            it.noteId == deletedNoteId
        }
        deletedNoteList.remove(deletedNote)
        _notes.value = deletedNoteList
    }

    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private var job: Job? = null

    init {
        job = viewModelScope.launch {
            dao.getNotesBySearchQuery("").collectLatest { notes ->
                _notes.value = notes
            }
        }
    }
}