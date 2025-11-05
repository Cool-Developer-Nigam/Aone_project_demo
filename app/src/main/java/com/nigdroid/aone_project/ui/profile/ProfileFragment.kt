package com.nigdroid.aone_project.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nigdroid.aone_project.databinding.FragmentProfileBinding
import com.nigdroid.aone_project.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        startAnimations()
    }

    private fun setupClickListeners() {
        binding.editProfileOption.setOnClickListener {
            animateOption(it)
            showComingSoonDialog("Edit Profile")
        }

        binding.changePasswordOption.setOnClickListener {
            animateOption(it)
            showComingSoonDialog("Change Password")
        }

        binding.aboutOption.setOnClickListener {
            animateOption(it)
            showAboutDialog()
        }

        binding.logoutOption.setOnClickListener {
            animateOption(it)
            showLogoutDialog()
        }
    }

    private fun startAnimations() {
        binding.profileCard.apply {
            alpha = 0f
            scaleX = 0.9f
            scaleY = 0.9f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start()
        }
    }

    private fun animateOption(view: View) {
        view.animate()
            .scaleX(0.98f)
            .scaleY(0.98f)
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

    private fun showComingSoonDialog(feature: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(feature)
            .setMessage("This feature is coming soon!")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("About Student Manager")
            .setMessage("""
                Student Manager v1.0
                
                A modern Android app for managing student records with ease.
                
                Features:
                • Add, Edit, Delete students
                • Beautiful Material Design UI
                • Real-time search
                • Cloud synchronization
                
                Developed by: Nigam Prasad Sahoo
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Clear any saved data (SharedPreferences, etc.)

        // Navigate to login screen
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}