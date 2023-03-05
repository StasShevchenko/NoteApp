package com.example.noteapp.presentation.notes_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.noteapp.data.model.Note
import com.example.noteapp.databinding.NoteItemBinding

class NotesAdapter(
    private val clickListener: OnNoteClickListener
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NotesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.NoteViewHolder {
        val binding =
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesAdapter.NoteViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = getItem(position)
                    if (currentItem != null) {
                        clickListener.onNoteClick(currentItem)
                    }
                }
            }
        }
        @SuppressLint("SetTextI18n")
        fun bind(note: Note){
            binding.apply {
                titleTextView.text = note.noteTitle
                contentTextView.text = note.noteContent
                dateCreatedTextView.text = "Создана: ${note.dateCreated}"
                if (note.isPinned) {
                    pinView.visibility = View.VISIBLE
                }
            }
        }
    }


    class NotesComparator : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    interface OnNoteClickListener{
        fun onNoteClick(note: Note)
    }
}