package com.nigdroid.aone_project.ui.students

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
class StudentListViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _students = MutableLiveData<List<StudentResponse>>()
    val students: LiveData<List<StudentResponse>> = _students

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val result = repository.getAllStudents()
            result.onSuccess { studentList ->
                _students.value = studentList
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _loading.value = false
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            val result = repository.deleteStudent(studentId)
            _deleteResult.value = result

            if (result.isSuccess) {
                loadStudents() // Refresh list after deletion
            }
        }
    }
}
