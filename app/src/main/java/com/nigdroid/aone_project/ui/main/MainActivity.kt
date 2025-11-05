package com.nigdroid.aone_project.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nigdroid.aone_project.R
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

        // Handle bottom navigation item selections manually for better control
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboardFragment -> {
                    if (navController.currentDestination?.id != R.id.dashboardFragment) {
                        try {
                            navController.navigate(R.id.dashboardFragment)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    true
                }
                R.id.studentListFragment -> {
                    if (navController.currentDestination?.id != R.id.studentListFragment) {
                        try {
                            navController.navigate(R.id.studentListFragment)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    true
                }
                R.id.profileFragment -> {
                    if (navController.currentDestination?.id != R.id.profileFragment) {
                        try {
                            navController.navigate(R.id.profileFragment)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    true
                }
                else -> false
            }
        }

        // Update toolbar title and bottom nav selection based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = when (destination.id) {
                R.id.dashboardFragment -> "Dashboard"
                R.id.studentListFragment -> "Students"
                R.id.addEditStudentFragment -> "Add Student"
                R.id.profileFragment -> "Profile"
                else -> "Student Manager"
            }

            // Update bottom navigation selection state
            when (destination.id) {
                R.id.dashboardFragment -> binding.bottomNavigation.menu.findItem(R.id.dashboardFragment)?.isChecked = true
                R.id.studentListFragment -> binding.bottomNavigation.menu.findItem(R.id.studentListFragment)?.isChecked = true
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Setup profile icon click
        binding.profileIcon.setOnClickListener {
            // Animate profile icon
            it.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()

                    // Navigate to profile fragment
                    try {
                        navController.navigate(R.id.profileFragment)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .start()
        }

        // Setup logout icon click
        binding.logoutIcon.setOnClickListener {
            // Animate logout icon
            it.animate()
                .rotation(360f)
                .setDuration(300)
                .withEndAction {
                    it.rotation = 0f
                    showLogoutDialog()
                }
                .start()
        }
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
                    try {
                        navController.navigate(R.id.addEditStudentFragment)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .start()
        }

        // Hide FAB on certain destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addEditStudentFragment, R.id.profileFragment -> binding.fabAddStudent.hide()
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
        // No menu needed as we're using custom icons
        return true
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // TODO: Clear user session/data
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