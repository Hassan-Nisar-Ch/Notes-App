package com.example.notesapp.data.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.data.model.Note
import com.example.notesapp.db.NoteDao
import jakarta.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    val allNotes = noteDao.getAllNotes()

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return noteDao.getNoteById(id)
    }
}