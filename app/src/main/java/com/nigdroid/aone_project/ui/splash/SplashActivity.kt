package com.nigdroid.aone_project.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nigdroid.aone_project.databinding.ActivitySplashBinding
import com.nigdroid.aone_project.ui.auth.LoginActivity
import com.nigdroid.aone_project.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    private var navigationHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide status bar for immersive experience
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )

        startAnimations()
        checkLoginAndNavigate()
    }

    private fun startAnimations() {
        // Logo scale and fade in animation
        val scaleX = ObjectAnimator.ofFloat(binding.logoCard, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.logoCard, "scaleY", 0f, 1f)
        val alpha = ObjectAnimator.ofFloat(binding.logoCard, "alpha", 0f, 1f)

        val logoAnimSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        // App name slide up animation
        val appNameSlideUp = ObjectAnimator.ofFloat(
            binding.appNameText,
            "translationY",
            100f,
            0f
        )
        val appNameFadeIn = ObjectAnimator.ofFloat(binding.appNameText, "alpha", 0f, 1f)

        val appNameAnimSet = AnimatorSet().apply {
            playTogether(appNameSlideUp, appNameFadeIn)
            duration = 600
            startDelay = 400
        }

        // Tagline fade in
        val taglineFadeIn = ObjectAnimator.ofFloat(binding.taglineText, "alpha", 0f, 1f)
        taglineFadeIn.apply {
            duration = 600
            startDelay = 800
        }

        // Play all animations
        AnimatorSet().apply {
            playSequentially(logoAnimSet, appNameAnimSet, taglineFadeIn)
            start()
        }

        // Rotate logo continuously
        binding.logoImage.apply {
            animate()
                .rotationBy(360f)
                .setDuration(2000)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    // Pulse effect
                    animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(500)
                        .withEndAction {
                            animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .start()
                        }
                        .start()
                }
                .start()
        }
    }

    private fun checkLoginAndNavigate() {
        lifecycleScope.launch {
            // Check login status using ViewModel
            val isLoggedIn = viewModel.isUserLoggedIn()
            val hasValidToken = viewModel.hasValidToken()

            // Both must be true for valid session
            val hasValidSession = isLoggedIn && hasValidToken

            // Show splash for 2.5 seconds minimum
            delay(2500)

            // Fade out animation before navigating
            binding.root.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    if (!navigationHandled) {
                        navigationHandled = true
                        if (hasValidSession) {
                            navigateToMain()
                        } else {
                            navigateToLogin()
                        }
                    }
                }
                .start()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any ongoing animations
        binding.logoImage.animate().cancel()
        binding.logoCard.animate().cancel()
        binding.appNameText.animate().cancel()
        binding.taglineText.animate().cancel()
        binding.root.animate().cancel()
    }
}