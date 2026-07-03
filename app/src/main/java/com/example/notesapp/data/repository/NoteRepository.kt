package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.example.notesapp.db.NoteDao
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    fun getNoteById(id: Int): Flow<Note> {
        return noteDao.getNoteById(id)
    }
}