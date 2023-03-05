package com.example.noteapp.presentation.add_edit_note_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.data_source.NotesDao
import com.example.noteapp.data.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteScreenViewModel @Inject constructor(
    private val dao: NotesDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentNoteId: Long = -1

    private val _currentNote: MutableStateFlow<Note?> = MutableStateFlow(null)
    val currentNote: StateFlow<Note?> = _currentNote

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var noteTitle = ""
    private var noteContent = ""

    var isPinned = false
        private set

    init {
        currentNoteId = savedStateHandle.get<Long>("noteId")!!
        if (currentNoteId != -1L) {
            viewModelScope.launch {
                _currentNote.value = dao.getNoteById(currentNoteId)
                noteTitle = currentNote.value!!.noteTitle
                noteContent = currentNote.value!!.noteContent
                isPinned = currentNote.value!!.isPinned
            }
        }
    }

    fun enterNoteTitle(noteTitle: String) {
        this.noteTitle = noteTitle
    }

    fun enterNoteContent(noteContent: String) {
        this.noteContent = noteContent
    }

    fun onBackPressed() {
        if (noteContent.isNotEmpty()) {
            viewModelScope.launch {
                if (currentNoteId == -1L) {
                    currentNoteId = 0
                }
                dao.insertNote(
                    Note(
                        noteId = currentNoteId,
                        noteTitle = noteTitle,
                        noteContent = noteContent,
                        isPinned = isPinned
                    )
                )
                _eventFlow.emit(UiEvent.NoteSaved)
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.NoteSaved)
            }
        }
    }

    fun pinNote() {
        isPinned = !isPinned
    }

    sealed class UiEvent {
        object NoteSaved : UiEvent()
    }
}