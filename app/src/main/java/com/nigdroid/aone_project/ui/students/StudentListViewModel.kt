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
class StudentListViewModel @Inject constructor(
    private val repository: StudentRepository
) : ViewModel() {

    val students: LiveData<List<Student>> = repository.allStudents

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                repository.deleteStudent(student)
                _deleteResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }
}
