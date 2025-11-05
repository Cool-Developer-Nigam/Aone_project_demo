package com.nigdroid.aone_project.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nigdroid.aone_project.data.local.PreferenceManager
import com.nigdroid.aone_project.data.model.LoginResponse
import com.nigdroid.aone_project.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.login(email, password)

            // Save user data if login successful
            result.onSuccess { response ->
                if (response.success && response.token != null && response.data != null) {
                    preferenceManager.saveUserData(
                        userId = response.data.id,
                        email = response.data.email,
                        token = response.token
                    )
                }
            }

            _loginResult.value = result
            _loading.value = false
        }
    }

    fun isUserLoggedIn(): Boolean {
        return preferenceManager.isLoggedIn()
    }

    fun hasValidToken(): Boolean {
        val token = preferenceManager.getAuthToken()
        return !token.isNullOrEmpty()
    }
}