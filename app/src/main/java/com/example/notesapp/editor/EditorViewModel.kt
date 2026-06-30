package com.example.notesapp.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class EditorViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    fun insertNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return noteRepository.getNoteById(id)
    }
}
