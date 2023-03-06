package com.example.noteapp.presentation.notes_screen

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.R
import com.example.noteapp.data.model.Note
import com.example.noteapp.databinding.NotesScreenFragmentBinding
import com.example.noteapp.presentation.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesScreenFragment : Fragment(R.layout.notes_screen_fragment), MainActivity.FabButtonClick,
    NotesAdapter.OnNoteClickListener {
    private val viewModel: NotesScreenViewModel by viewModels()
    private lateinit var binding: NotesScreenFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setFabListener(this)
         binding = NotesScreenFragmentBinding.bind(view)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        binding.topAppBar
        val deletedNote =
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("noteId")
        deletedNote?.observe(viewLifecycleOwner) { deletedNoteId ->
            deletedNoteId?.let {
                viewModel.hideNote(deletedNoteId)
                val snackbar =
                    Snackbar.make(requireView(), "Заметка была удалена!", Snackbar.LENGTH_SHORT)
                snackbar.setAction("Отменить") {
                    viewModel.restoreNote()
                }
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event != DISMISS_EVENT_ACTION) {
                            viewModel.deleteNote(deletedNoteId)
                        }
                    }
                })
                snackbar.show()
                navController.currentBackStackEntry?.savedStateHandle?.set<Long>("noteId", null)
            }
        }


        val notesAdapter = NotesAdapter(this)
        binding.apply {
            notesRecyclerView.apply {
                adapter = notesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            searchImageButton.setOnClickListener {
                toolbarTitle.visibility = View.GONE
                backImageButton.visibility = View.VISIBLE
                searchEditText.visibility = View.VISIBLE
                searchImageButton.visibility = View.GONE
            }
            backImageButton.setOnClickListener {
                toolbarTitle.visibility = View.VISIBLE
                backImageButton.visibility = View.GONE
                searchEditText.visibility = View.GONE
                viewModel.searchNotes("")
                searchImageButton.visibility = View.VISIBLE
            }
            searchEditText.addTextChangedListener {text ->
                viewModel.searchNotes(text.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collectLatest { notes ->
                    notesAdapter.submitList(notes)
                }
            }
        }
    }




    override fun onFabClicked() {
        binding.toolbarTitle.visibility = View.GONE
        val action =
            NotesScreenFragmentDirections.actionNotesScreenFragmentToAddEditNoteScreenFragment()
        findNavController().navigate(action)
    }

    override fun onNoteClick(note: Note) {
        binding.toolbarTitle.visibility = View.GONE
        val action =
            NotesScreenFragmentDirections.actionNotesScreenFragmentToAddEditNoteScreenFragment(note.noteId)
        findNavController().navigate(action)
    }
}