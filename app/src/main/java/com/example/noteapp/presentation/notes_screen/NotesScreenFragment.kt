package com.example.noteapp.presentation.notes_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.R
import com.example.noteapp.databinding.NotesScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesScreenFragment : Fragment(R.layout.notes_screen_fragment) {
    private val viewModel: NotesScreenViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding: NotesScreenFragmentBinding = NotesScreenFragmentBinding.bind(view)
        activity?.title = "Заметки"
        val notesAdapter = NotesAdapter()
        binding.apply {
            notesRecyclerView.apply {
                adapter = notesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
}