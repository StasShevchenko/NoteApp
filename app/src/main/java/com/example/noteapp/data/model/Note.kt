package com.example.noteapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0,
    val noteTitle: String,
    val noteContent: String,
    val isPinned: Boolean = false,
    val dateCreated: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
)
