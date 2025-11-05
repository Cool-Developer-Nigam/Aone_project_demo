package com.nigdroid.aone_project.ui.dashboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nigdroid.aone_project.R
import com.nigdroid.aone_project.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    private var pulseRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        startAnimations()
    }

    private fun setupObservers() {
        viewModel.students.observe(viewLifecycleOwner) { students ->
            binding.progressBar.visibility = View.GONE

            // Update total students count
            binding.totalStudentsCount.text = students.size.toString()
            animateCount(binding.totalStudentsCount, students.size)

            // Calculate unique classes
            val uniqueClasses = students.map { it.className }.distinct().size
            binding.activeClassesCount.text = uniqueClasses.toString()
            animateCount(binding.activeClassesCount, uniqueClasses)

            // Calculate this month additions (mock - you can add timestamp logic)
            val thisMonth = (students.size * 0.3).toInt()
            binding.thisMonthCount.text = thisMonth.toString()
            animateCount(binding.thisMonthCount, thisMonth)

            // Recent activity
            binding.recentActivityCount.text = students.size.coerceAtMost(10).toString()
            animateCount(binding.recentActivityCount, students.size.coerceAtMost(10))
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.viewAllStudentsButton.setOnClickListener {
            animateButton(it)
            findNavController().navigate(R.id.action_dashboard_to_studentList)
        }

        binding.addStudentButton.setOnClickListener {
            animateButton(it)
            findNavController().navigate(R.id.action_dashboard_to_addStudent)
        }

        // Stats card click listeners
        binding.totalStudentsCard.setOnClickListener {
            animateCard(it)
            findNavController().navigate(R.id.action_dashboard_to_studentList)
        }

        binding.notificationIcon.setOnClickListener {
            animateNotification()
        }
    }

    private fun startAnimations() {
        // Animate welcome card
        binding.welcomeCard.apply {
            alpha = 0f
            translationY = -50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Animate stats cards with stagger effect
        val cards = listOf(
            binding.totalStudentsCard,
            binding.activeClassesCard,
            binding.thisMonthCard,
            binding.recentActivityCard
        )

        cards.forEachIndexed { index, card ->
            card.apply {
                alpha = 0f
                scaleX = 0.8f
                scaleY = 0.8f
                postDelayed({
                    animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(500)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }, 200L * index)
            }
        }

        // Animate quick actions
        binding.quickActionsLayout.apply {
            alpha = 0f
            translationY = 50f
            postDelayed({
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }, 800)
        }

        // Continuous pulse animation for notification icon
        startNotificationPulse()
    }

    private fun animateCount(textView: android.widget.TextView, targetCount: Int) {
        val animator = android.animation.ValueAnimator.ofInt(0, targetCount)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
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

    private fun animateCard(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(150)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun startNotificationPulse() {
        pulseRunnable = object : Runnable {
            override fun run() {
                // Check if binding is still valid
                if (_binding == null) return

                binding.notificationIcon.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(800)
                    .withEndAction {
                        // Check again before accessing binding
                        if (_binding == null) return@withEndAction

                        binding.notificationIcon.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(800)
                            .start()
                    }
                    .start()

                // Check before posting delayed
                if (_binding != null) {
                    binding.notificationIcon.postDelayed(this, 5000)
                }
            }
        }

        binding.notificationIcon.postDelayed(pulseRunnable!!, 2000)
    }

    private fun animateNotification() {
        val rotation = ObjectAnimator.ofFloat(binding.notificationIcon, "rotation", 0f, 25f, -25f, 0f)
        rotation.duration = 500
        rotation.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Remove all pending callbacks to prevent crashes
        pulseRunnable?.let {
            binding.notificationIcon.removeCallbacks(it)
        }

        // Cancel all animations
        binding.notificationIcon.animate().cancel()
        binding.welcomeCard.animate().cancel()
        binding.quickActionsLayout.animate().cancel()

        _binding = null
    }
}