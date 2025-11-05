package com.nigdroid.aone_project.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nigdroid.aone_project.data.model.StudentResponse
import com.nigdroid.aone_project.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _students = MutableLiveData<List<StudentResponse>>()
    val students: LiveData<List<StudentResponse>> = _students

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getAllStudents()
            result.onSuccess { studentList ->
                _students.value = studentList
            }.onFailure { exception ->
                _error.value = exception.message
                _students.value = emptyList() // Show 0 counts on error
            }

            _loading.value = false
        }
    }

    fun refresh() {
        loadStudents()
    }
}