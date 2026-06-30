package com.example.notesapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    val allNotes: LiveData<List<Note>> = noteRepository.allNotes

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
}
