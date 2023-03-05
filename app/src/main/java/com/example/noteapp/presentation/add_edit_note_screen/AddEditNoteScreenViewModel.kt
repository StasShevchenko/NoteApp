package com.example.noteapp.presentation.add_edit_note_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.noteapp.data.data_source.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditNoteScreenViewModel @Inject constructor(
    private val dao: NotesDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


}