package com.example.notesapp.editor

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentEditorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by viewModels()
    val args: EditorFragmentArgs by navArgs()

    private var noteId = -1

    private var originalTitle = ""
    private var originalContent = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteId = args.noteId

        if (noteId != -1) {
            viewModel.getNoteById(noteId).observe(viewLifecycleOwner) {
                binding.apply {
                    etTitle.setText(it.title)
                    etNote.setText(it.content)
                    originalTitle = it.title
                    originalContent = it.content
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleExit()
                }
            }
        )

        binding.cardBack.setOnClickListener {
            handleExit()
        }

        binding.cardSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etNote.text.toString().trim()
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter both title and content.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (noteId == -1) {
                viewModel.insertNote(Note(title = title, content = content))
            } else {
                viewModel.updateNote(Note(id = noteId, title = title, content = content))
            }

            findNavController().popBackStack()
        }

    }

    private fun hasUnsavedChanges(): Boolean {

        val currentTitle = binding.etTitle.text.toString().trim()
        val currentContent = binding.etNote.text.toString().trim()

        return currentTitle != originalTitle ||
                currentContent != originalContent
    }

    private fun handleExit() {

        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showUnsavedChangesDialog() {

        val builder = AlertDialog.Builder(requireContext())

        builder
            .setTitle("Unsaved Changes")
            .setMessage("You have unsaved changes. Discard them?")
            .setPositiveButton("Discard") { _, _ ->

                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

        val alert  = builder.create()
        alert.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}