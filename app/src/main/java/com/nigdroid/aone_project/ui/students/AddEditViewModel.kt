package com.nigdroid.aone_project.ui.students

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nigdroid.aone_project.data.model.StudentRequest
import com.nigdroid.aone_project.data.model.StudentResponse
import com.nigdroid.aone_project.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _student = MutableLiveData<StudentResponse?>()
    val student: LiveData<StudentResponse?> = _student

    fun loadStudent(studentId: Int) {
        viewModelScope.launch {
            _loading.value = true

            val result = repository.getAllStudents()
            result.onSuccess { students ->
                val foundStudent = students.find { it.id == studentId }
                _student.value = foundStudent
            }.onFailure {
                _student.value = null
            }

            _loading.value = false
        }
    }

    fun saveStudent(
        id: Int?,
        name: String,
        className: String,
        rollNo: String,
        contact: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            val student = StudentRequest(
                id = id,
                name = name,
                `class` = className,
                roll_no = rollNo,
                contact = contact
            )

            val result = if (id == null || id == -1) {
                repository.addStudent(student)
            } else {
                repository.updateStudent(student)
            }

            _saveResult.value = if (result.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }

            _loading.value = false
        }
    }

    fun validateInput(
        name: String,
        className: String,
        rollNo: String,
        contact: String
    ): String? {
        return when {
            name.isBlank() -> "Please enter student name"
            className.isBlank() -> "Please enter class"
            rollNo.isBlank() -> "Please enter roll number"
            contact.isBlank() -> "Please enter contact number"
            contact.length != 10 -> "Contact must be 10 digits"
            !contact.all { it.isDigit() } -> "Contact must contain only numbers"
            else -> null
        }
    }
}