package com.nigdroid.aone_project.ui.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nigdroid.aone_project.databinding.FragmentAddEditStudentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditStudentFragment : Fragment() {

    private var _binding: FragmentAddEditStudentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditViewModel by viewModels()

    private var studentId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentId = arguments?.getInt("studentId", -1) ?: -1
        isEditMode = studentId != -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupClickListeners()
        startAnimations()

        if (isEditMode) {
            loadStudentData()
        }
    }

    private fun setupUI() {
        // Update title based on mode
        binding.formTitle.text = if (isEditMode) "Edit Student" else "Add New Student"
        binding.saveButton.text = if (isEditMode) "Update" else "Save"
    }

    private fun setupObservers() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val message = if (isEditMode) {
                    "Student updated successfully!"
                } else {
                    "Student added successfully!"
                }
                showSuccessAndNavigateBack(message)
            }.onFailure { exception ->
                showError(exception.message ?: "Failed to save student")
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.saveButton.isEnabled = false
                binding.cancelButton.isEnabled = false
                disableInputs()
            } else {
                binding.progressBar.visibility = View.GONE
                binding.saveButton.isEnabled = true
                binding.cancelButton.isEnabled = true
                enableInputs()
            }
        }

        // Load student data for edit mode
        if (isEditMode) {
            viewModel.student.observe(viewLifecycleOwner) { student ->
                student?.let {
                    binding.nameInput.setText(it.name)
                    binding.classInput.setText(it.className)
                    binding.rollNoInput.setText(it.rollNo)
                    binding.contactInput.setText(it.contact)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            animateButton(it)
            validateAndSave()
        }

        binding.cancelButton.setOnClickListener {
            animateButton(it)
            findNavController().navigateUp()
        }
    }

    private fun validateAndSave() {
        // Clear previous errors
        clearErrors()

        val name = binding.nameInput.text.toString().trim()
        val className = binding.classInput.text.toString().trim()
        val rollNo = binding.rollNoInput.text.toString().trim()
        val contact = binding.contactInput.text.toString().trim()

        // Validate input
        val error = viewModel.validateInput(name, className, rollNo, contact)
        if (error != null) {
            showValidationError(error)
            shakeForm()
            return
        }

        // Save student
        viewModel.saveStudent(
            id = if (isEditMode) studentId else null,
            name = name,
            className = className,
            rollNo = rollNo,
            contact = contact
        )
    }

    private fun showValidationError(error: String) {
        when {
            error.contains("name", ignoreCase = true) -> {
                binding.nameInputLayout.error = error
                binding.nameInput.requestFocus()
            }
            error.contains("class", ignoreCase = true) -> {
                binding.classInputLayout.error = error
                binding.classInput.requestFocus()
            }
            error.contains("roll", ignoreCase = true) -> {
                binding.rollNoInputLayout.error = error
                binding.rollNoInput.requestFocus()
            }
            error.contains("contact", ignoreCase = true) -> {
                binding.contactInputLayout.error = error
                binding.contactInput.requestFocus()
            }
        }
    }

    private fun clearErrors() {
        binding.nameInputLayout.error = null
        binding.classInputLayout.error = null
        binding.rollNoInputLayout.error = null
        binding.contactInputLayout.error = null
    }

    private fun loadStudentData() {
        viewModel.loadStudent(studentId)
    }

    private fun startAnimations() {
        // Animate form card
        binding.formCard.apply {
            alpha = 0f
            translationY = 100f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .start()
        }

        // Animate input fields with stagger
        val inputs = listOf(
            binding.nameInputLayout,
            binding.classInputLayout,
            binding.rollNoInputLayout,
            binding.contactInputLayout
        )

        inputs.forEachIndexed { index, layout ->
            layout.apply {
                alpha = 0f
                translationX = -50f
                postDelayed({
                    animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(400)
                        .start()
                }, 100L * index)
            }
        }
    }

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun shakeForm() {
        val shake = android.view.animation.AnimationUtils.loadAnimation(
            requireContext(),
            com.nigdroid.aone_project.R.anim.shake
        )
        binding.formCard.startAnimation(shake)
    }

    private fun disableInputs() {
        binding.nameInput.isEnabled = false
        binding.classInput.isEnabled = false
        binding.rollNoInput.isEnabled = false
        binding.contactInput.isEnabled = false
    }

    private fun enableInputs() {
        binding.nameInput.isEnabled = true
        binding.classInput.isEnabled = true
        binding.rollNoInput.isEnabled = true
        binding.contactInput.isEnabled = true
    }

    private fun showSuccessAndNavigateBack(message: String) {
        // Animate form out
        binding.formCard.animate()
            .alpha(0f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(300)
            .withEndAction {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(
                        com.nigdroid.aone_project.R.color.success_green,
                        null
                    ))
                    .show()

                findNavController().navigateUp()
            }
            .start()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(
                com.nigdroid.aone_project.R.color.error_red,
                null
            ))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}