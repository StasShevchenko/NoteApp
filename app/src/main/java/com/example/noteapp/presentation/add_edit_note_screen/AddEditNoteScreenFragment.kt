package com.example.noteapp.presentation.add_edit_note_screen

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.noteapp.R
import com.example.noteapp.databinding.AddEditNoteScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditNoteScreenFragment : Fragment(R.layout.add_edit_note_screen_fragment) {
    private val viewModel: AddEditNoteScreenViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AddEditNoteScreenFragmentBinding.bind(view)

        binding.apply {
            titleEditText.addTextChangedListener {
                viewModel.enterNoteTitle(it.toString())
            }
            contentEditText.addTextChangedListener {
                viewModel.enterNoteContent(it.toString())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentNote.collectLatest { note ->
                    note?.let { note ->
                        binding.titleEditText.setText(note.noteTitle)
                        binding.contentEditText.setText(note.noteContent)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    AddEditNoteScreenViewModel.UiEvent.NoteSaved -> findNavController().popBackStack()
                }
            }
        }

        onBackPressedCustomAction {
            viewModel.onBackPressed()
        }
    }


}

fun Fragment.onBackPressedCustomAction(action: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action()
            }
        })
}