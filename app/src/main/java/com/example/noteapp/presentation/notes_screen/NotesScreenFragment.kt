package com.example.noteapp.presentation.notes_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.noteapp.R

class NotesScreenFragment : Fragment(R.layout.notes_screen_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Заметки"
    }
}