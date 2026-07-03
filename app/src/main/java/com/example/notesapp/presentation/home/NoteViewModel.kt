package com.example.notesapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    // No need for _uiState bcz uiState is already immutable (StateFlow)
    val uiState: StateFlow<HomeUiState> = combine(
        noteRepository.allNotes,
        _searchQuery
    ) { notes, query ->
        val filteredNotes = if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
        HomeUiState(notes = filteredNotes, isEmpty = filteredNotes.isEmpty())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onAddNoteClicked() {
        viewModelScope.launch { _events.emit(HomeEvent.NavigateToEditor(null)) }
    }

    fun onNoteClicked(note: Note) {
        viewModelScope.launch { _events.emit(HomeEvent.NavigateToEditor(note)) }
    }

    fun onDeleteNoteClicked(note: Note) {
        viewModelScope.launch { _events.emit(HomeEvent.ShowDeleteConfirmation(note)) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }
}

data class HomeUiState(
    val notes: List<Note> = emptyList(),
    val isEmpty: Boolean = false
)

sealed class HomeEvent {
    data class NavigateToEditor(val note: Note?) : HomeEvent()
    data class ShowDeleteConfirmation(val note: Note) : HomeEvent()
}
