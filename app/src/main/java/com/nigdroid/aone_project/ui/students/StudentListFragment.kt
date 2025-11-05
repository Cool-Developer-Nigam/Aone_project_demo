package com.nigdroid.aone_project.ui.students

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nigdroid.aone_project.R
import com.nigdroid.aone_project.data.model.StudentResponse
import com.nigdroid.aone_project.databinding.FragmentStudentListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentListViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter
    private var allStudents = listOf<StudentResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        setupSwipeRefresh()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onEditClick = { student ->
                navigateToEdit(student)
            },
            onDeleteClick = { student ->
                showDeleteDialog(student)
            },
            onItemClick = { student ->
                // Show student details (optional)
                showStudentDetails(student)
            }
        )

        binding.studentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filterStudents(query)

                // Show/hide clear button
                binding.clearSearchButton.visibility =
                    if (query.isEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.text.clear()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            resources.getColor(R.color.purple_light, null),
            resources.getColor(R.color.accent_pink, null),
            resources.getColor(R.color.accent_blue, null)
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadStudents()
        }
    }

    private fun setupObservers() {
        viewModel.students.observe(viewLifecycleOwner) { students ->
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.GONE

            allStudents = students

            if (students.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                studentAdapter.submitList(students)
                animateRecyclerView()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && allStudents.isEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                hideEmptyState()
                hideErrorState()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.GONE

            error?.let {
                if (allStudents.isEmpty()) {
                    showErrorState(it)
                } else {
                    showSnackbar(it)
                }
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showSnackbar("Student deleted successfully!")
            }.onFailure { exception ->
                showSnackbar("Failed to delete: ${exception.message}")
            }
        }
    }

    private fun setupClickListeners() {
        binding.addFirstStudentButton.setOnClickListener {
            navigateToAdd()
        }

        binding.retryButton.setOnClickListener {
            hideErrorState()
            viewModel.loadStudents()
        }
    }

    private fun filterStudents(query: String) {
        if (query.isEmpty()) {
            studentAdapter.submitList(allStudents)
        } else {
            val filteredList = allStudents.filter { student ->
                student.name.contains(query, ignoreCase = true) ||
                        student.rollNo.contains(query, ignoreCase = true) ||
                        student.className.contains(query, ignoreCase = true) ||
                        student.contact.contains(query, ignoreCase = true)
            }
            studentAdapter.submitList(filteredList)

            if (filteredList.isEmpty() && allStudents.isNotEmpty()) {
                showSnackbar("No students found for '$query'")
            }
        }
    }

    private fun showDeleteDialog(student: StudentResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Student?")
            .setMessage("Are you sure you want to delete ${student.name}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteStudent(student.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showStudentDetails(student: StudentResponse) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(student.name)
            .setMessage(
                """
                Roll Number: ${student.rollNo}
                Class: ${student.className}
                Contact: ${student.contact}
                """.trimIndent()
            )
            .setPositiveButton("Edit") { _, _ ->
                navigateToEdit(student)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun navigateToAdd() {
        findNavController().navigate(
            R.id.action_studentList_to_addEditStudent
        )
    }

    private fun navigateToEdit(student: StudentResponse) {
        val bundle = bundleOf("studentId" to student.id)
        findNavController().navigate(
            R.id.action_studentList_to_addEditStudent,
            bundle
        )
    }

    private fun showEmptyState() {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.studentsRecyclerView.visibility = View.GONE
        binding.errorStateLayout.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.studentsRecyclerView.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        binding.errorStateLayout.visibility = View.VISIBLE
        binding.errorMessageText.text = message
        binding.studentsRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun hideErrorState() {
        binding.errorStateLayout.visibility = View.GONE
    }

    private fun animateRecyclerView() {
        binding.studentsRecyclerView.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.purple_primary, null))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}