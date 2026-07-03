package com.example.notesapp.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditorEvent>()
    val events = _events.asSharedFlow()

    private val note: Note? = savedStateHandle.get<Note>("note")
    private var originalNote: Note? = note

    init {
        note?.let {
            _uiState.value = EditorUiState(title = it.title, content = it.content)
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onContentChanged(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun onSaveClicked() {
        val title = _uiState.value.title.trim()
        val content = _uiState.value.content.trim()

        if (title.isEmpty() || content.isEmpty()) {
            viewModelScope.launch { _events.emit(EditorEvent.ShowToast("Please enter both title and content.")) }
            return
        }

        viewModelScope.launch {
            if (note == null) {
                noteRepository.insertNote(Note(title = title, content = content))
            } else {
                noteRepository.updateNote(note.copy(title = title, content = content))
            }
            _events.emit(EditorEvent.NavigateBack)
        }
    }

    fun onBackClicked() {
        if (hasUnsavedChanges()) {
            viewModelScope.launch { _events.emit(EditorEvent.ShowUnsavedChangesDialog) }
        } else {
            viewModelScope.launch { _events.emit(EditorEvent.NavigateBack) }
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val currentTitle = _uiState.value.title.trim()
        val currentContent = _uiState.value.content.trim()

        val origTitle = originalNote?.title?.trim() ?: ""
        val origContent = originalNote?.content?.trim() ?: ""

        return currentTitle != origTitle || currentContent != origContent
    }
}

data class EditorUiState(
    val title: String = "",
    val content: String = ""
)

sealed class EditorEvent {
    data class ShowToast(val message: String) : EditorEvent()
    object NavigateBack : EditorEvent()
    object ShowUnsavedChangesDialog : EditorEvent()
}
