package com.example.notesapp.presentation.editor

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentEditorBinding
import com.example.notesapp.util.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackClicked()
                }
            }
        )
    }

    private fun setupListeners() {
        binding.apply {
            cardBack.setOnClickListener {
                viewModel.onBackClicked()
            }

            cardSave.setOnClickListener {
                viewModel.onSaveClicked()
            }

            etTitle.doOnTextChanged { text, _, _, _ ->
                if (etTitle.hasFocus()) {
                    viewModel.onTitleChanged(text.toString())
                }
            }

            etNote.doOnTextChanged { text, _, _, _ ->
                if (etNote.hasFocus()) {
                    viewModel.onContentChanged(text.toString())
                }
            }
        }
    }

    private fun observeViewModel() {
        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { state ->
                binding.apply {
                    etTitle.setText(state.title)
                    etNote.setText(state.content)
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.events.collect { event ->
                when (event) {
                    is EditorEvent.ShowToast -> {
                        Toast.makeText(requireContext(),
                            getString(R.string.enter_title_and_content), Toast.LENGTH_SHORT)
                            .show()
                    }

                    is EditorEvent.NavigateBack -> {
                        findNavController().popBackStack()
                    }

                    is EditorEvent.ShowUnsavedChangesDialog -> {
                        showUnsavedChangesDialog()
                    }
                }
            }
        }
    }

    private fun showUnsavedChangesDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.unsaved_changes))
            .setMessage(getString(R.string.unsaved_changes_confirmation))
            .setPositiveButton(getString(R.string.discard)) { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
