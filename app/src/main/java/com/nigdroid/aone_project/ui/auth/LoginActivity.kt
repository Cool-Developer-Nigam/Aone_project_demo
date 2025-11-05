package com.nigdroid.aone_project.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nigdroid.aone_project.ui.main.MainActivity
import com.nigdroid.aone_project.R
import com.nigdroid.aone_project.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        startAnimations()
    }

    private fun setupUI() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (validateInput(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.forgotPasswordText.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Contact admin for password reset",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        // Animate login card on focus
        binding.emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) animateCard()
        }

        binding.passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) animateCard()
        }
    }

    private fun setupObservers() {
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.isEnabled = false
                binding.loginButton.text = "Logging in..."
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "Login"
            }
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                showSuccessAnimation()
                navigateToMain()
            }.onFailure { exception ->
                showError(exception.message ?: "Login failed")
                shakeAnimation()
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }

    private fun startAnimations() {
        // Fade in animation for welcome text
        binding.welcomeText.alpha = 0f
        binding.welcomeText.translationY = -50f
        binding.welcomeText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .start()

        // Fade in animation for subtitle
        binding.subtitleText.alpha = 0f
        binding.subtitleText.animate()
            .alpha(1f)
            .setStartDelay(200)
            .setDuration(600)
            .start()

        // Slide up animation for login card
        binding.loginCard.translationY = 100f
        binding.loginCard.alpha = 0f
        binding.loginCard.animate()
            .translationY(0f)
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(700)
            .start()

        // Rotate floating circles
        binding.circle1.animate()
            .rotationBy(360f)
            .setDuration(20000)
            .withEndAction {
                binding.circle1.animate()
                    .rotationBy(360f)
                    .setDuration(20000)
                    .start()
            }
            .start()

        binding.circle2.animate()
            .rotationBy(-360f)
            .setDuration(25000)
            .withEndAction {
                binding.circle2.animate()
                    .rotationBy(-360f)
                    .setDuration(25000)
                    .start()
            }
            .start()
    }

    private fun animateCard() {
        binding.loginCard.animate()
            .scaleX(1.02f)
            .scaleY(1.02f)
            .setDuration(200)
            .withEndAction {
                binding.loginCard.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }

    private fun shakeAnimation() {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        binding.loginCard.startAnimation(shake)
    }

    private fun showSuccessAnimation() {
        binding.loginCard.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .alpha(0.8f)
            .setDuration(150)
            .withEndAction {
                binding.loginCard.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.error_red))
            .setTextColor(getColor(android.R.color.white))
            .show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}