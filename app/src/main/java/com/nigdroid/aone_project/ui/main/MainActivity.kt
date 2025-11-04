package com.nigdroid.aone_project

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nigdroid.aone_project.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupToolbar()
        setupFAB()
        setupAnimations()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation with nav controller
        binding.bottomNavigation.setupWithNavController(navController)

        // Update toolbar title based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = when (destination.id) {
                R.id.dashboardFragment -> "Dashboard"
                R.id.studentListFragment -> "Students"
                R.id.addEditStudentFragment -> "Add Student"
                else -> "Student Manager"
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupFAB() {
        binding.fabAddStudent.setOnClickListener {
            // Animate FAB
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()

                    // Navigate to add student
                    navController.navigate(R.id.addEditStudentFragment)
                }
                .start()
        }

        // Hide FAB on certain destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addEditStudentFragment -> binding.fabAddStudent.hide()
                else -> binding.fabAddStudent.show()
            }
        }
    }

    private fun setupAnimations() {
        // Pulse animation for FAB
        binding.fabAddStudent.postDelayed({
            startFABPulse()
        }, 1000)
    }

    private fun startFABPulse() {
        binding.fabAddStudent.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(800)
            .withEndAction {
                binding.fabAddStudent.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(800)
                    .withEndAction {
                        // Repeat pulse after delay
                        binding.fabAddStudent.postDelayed({
                            startFABPulse()
                        }, 3000)
                    }
                    .start()
            }
            .start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // TODO: Implement search
                true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Settings")
            .setMessage("Settings feature coming soon!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}