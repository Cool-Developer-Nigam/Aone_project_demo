package com.nigdroid.aone_project.ui.students

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nigdroid.aone_project.data.model.Student
import com.nigdroid.aone_project.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    fun loadStudent(studentId: Int) {
        viewModelScope.launch {
            _student.value = repository.getStudentById(studentId)
        }
    }

    fun saveStudent(student: Student) {
        viewModelScope.launch {
            try {
                if (student.id == 0) {
                    repository.insertStudent(student)
                } else {
                    repository.updateStudent(student)
                }
                _saveResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun validateInput(name: String, className: String, rollNo: String, contact: String): String? {
        return when {
            name.isBlank() -> "Please enter student name"
            className.isBlank() -> "Please enter class"
            rollNo.isBlank() -> "Please enter roll number"
            contact.isBlank() -> "Please enter contact number"
            contact.length != 10 -> "Contact must be 10 digits"
            else -> null
        }
    }
}