package com.example.notesapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemNoteBinding
import com.example.notesapp.data.model.Note

class NoteAdapter(
    private val onDeleteItemClick: (Note) -> Unit,
    private val onItemClick: (id: Int) -> Unit
) : ListAdapter<Note, NoteAdapter.ViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)

        holder.binding.title.text = note.title
        holder.binding.cardNote.setOnClickListener { onItemClick(note.id) }

        holder.binding.ivDelete.setOnClickListener {
            onDeleteItemClick(note)
        }
    }


    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

}