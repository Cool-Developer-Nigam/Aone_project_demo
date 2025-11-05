package com.nigdroid.aone_project.ui.splash

import androidx.lifecycle.ViewModel
import com.nigdroid.aone_project.data.local.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return preferenceManager.isLoggedIn()
    }

    fun hasValidToken(): Boolean {
        val token = preferenceManager.getAuthToken()
        return !token.isNullOrEmpty()
    }
}