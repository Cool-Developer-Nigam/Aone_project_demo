package com.nigdroid.aone_project.utils

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nigdroid.aone_project.R

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.fadeIn() {
    alpha = 0f
    show()
    animate()
        .alpha(1f)
        .setDuration(300)
        .start()
}

fun View.fadeOut() {
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction { hide() }
        .start()
}

fun View.slideUp() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
    startAnimation(animation)
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showSnackbar(message: String) {
    view?.let {
        Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
    }
}