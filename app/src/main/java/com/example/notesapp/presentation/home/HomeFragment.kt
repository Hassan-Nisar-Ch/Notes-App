package com.example.notesapp.presentation.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.util.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()

    private val adapter = NoteAdapter(
        onDeleteItemClick = { viewModel.onDeleteNoteClicked(it) },
        onItemClick = { viewModel.onNoteClicked(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvNotes.adapter = adapter

            cardInfo.setOnClickListener {
                showInfoDialog()
            }

            fabAdd.setOnClickListener {
                viewModel.onAddNoteClicked()
            }

            searchBar.doOnTextChanged { text, _, _, _ ->
                viewModel.onSearchQueryChanged(text.toString())
            }
        }

        observeViewModel(adapter)
    }

    private fun observeViewModel(adapter: NoteAdapter) {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.notes)
                    binding.layoutEmpty.visibility =
                        if (state.isEmpty) View.VISIBLE else View.GONE
                }
            }

            launch {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeEvent.NavigateToEditor -> {
                            val action = HomeFragmentDirections.actionHomeFragmentToEditorFragment(event.note)
                            findNavController().navigate(action)
                        }

                        is HomeEvent.ShowDeleteConfirmation -> {
                            showDeleteDialog(event.note)
                        }
                    }
                }
            }
        }
    }

    private fun showInfoDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_info)
        dialog.window?.attributes?.windowAnimations = R.style.animation
        dialog.window?.setLayout(600, 532)
        dialog.show()
    }

    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to Delete?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteNote(note)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
