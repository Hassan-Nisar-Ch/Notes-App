package com.example.notesapp.home

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var originalList = listOf<Note>()

        val dialogInfo = Dialog(requireContext())

        val onDeleteClick = { note: Note ->

            val builder = AlertDialog.Builder(requireContext())

            builder
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to Delete?")
                .setPositiveButton("Yes") { _, _ ->

                    viewModel.deleteNote(note)
                }
                .setNegativeButton("No") { dialog, _ ->

                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }

        val adapter = NoteAdapter(
            onDeleteClick
        ) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToEditorFragment(it)
            )
        }

        binding.apply {

            cardInfo.setOnClickListener {
                dialogInfo.setContentView(R.layout.dialog_info)
                dialogInfo.window?.attributes?.windowAnimations = R.style.animation
                dialogInfo.window?.setLayout(600, 532)
                dialogInfo.show()
            }

            fabAdd.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_editorFragment)
            }

            rvNotes.adapter = adapter

            searchBar.doOnTextChanged { text, _, _, _ ->

                val query = text.toString().trim()

                if (query.isEmpty()) {

                    adapter.submitList(originalList)

                } else {

                    val filteredList = originalList.filter {

                        it.title.contains(query, ignoreCase = true) ||
                                it.content.contains(query, ignoreCase = true)
                    }

                    adapter.submitList(filteredList)
                }
            }

            viewModel.allNotes.observe(viewLifecycleOwner) {
                originalList = it
                adapter.submitList(it)
                binding.layoutEmpty.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.searchBar.setText("")
    }
}