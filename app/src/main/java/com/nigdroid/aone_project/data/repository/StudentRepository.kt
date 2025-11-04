package com.nigdroid.aone_project.data.repository

import androidx.lifecycle.LiveData
import com.nigdroid.aone_project.data.local.StudentDao
import com.nigdroid.aone_project.data.model.Student
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val studentDao: StudentDao
) {

    val allStudents: LiveData<List<Student>> = studentDao.getAllStudents()
    val studentCount: LiveData<Int> = studentDao.getStudentCount()

    suspend fun insertStudent(student: Student) = studentDao.insertStudent(student)

    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)

    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    suspend fun getStudentById(id: Int) = studentDao.getStudentById(id)

    // Dummy login validation
    fun validateLogin(email: String, password: String): Boolean {
        return email == "admin@student.com" && password == "admin123"
    }
}