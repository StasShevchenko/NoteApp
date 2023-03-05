package com.example.noteapp.presentation.add_edit_note_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.noteapp.R
import com.example.noteapp.databinding.AddEditNoteScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddEditNoteScreenFragment : Fragment(R.layout.add_edit_note_screen_fragment) {
    private val viewModel: AddEditNoteScreenViewModel by viewModels()
    private lateinit var menu: Menu
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AddEditNoteScreenFragmentBinding.bind(view)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        val filledPinIcon = resources.getDrawable(R.drawable.ic_filled_pin)
        filledPinIcon.alpha = 255
        val outlinedPinIcon = resources.getDrawable(R.drawable.ic_outlined_pin)
        binding.topAppBar.menu.getItem(0).icon = outlinedPinIcon
        binding.topAppBar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.pin -> {
                    viewModel.pinNote()
                    if (viewModel.isPinned) {
                        binding.topAppBar.menu.getItem(0).icon = filledPinIcon
                    } else{
                        binding.topAppBar.menu.getItem(0).icon = outlinedPinIcon
                    }
                    true
                }
                R.id.delete ->{
                    true
                }
                else -> {
                    true
                }
            }
        }
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
                        if (note.isPinned) {
                            binding.topAppBar.menu.getItem(0).icon = filledPinIcon
                        } else{
                            binding.topAppBar.menu.getItem(0).icon = outlinedPinIcon
                        }
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