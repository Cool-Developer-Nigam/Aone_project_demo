package com.nigdroid.aone_project.data.repository

import com.nigdroid.aone_project.data.model.*
import com.nigdroid.aone_project.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Login with API
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Login failed"))
                    }
                } else {
                    Result.failure(Exception("Server error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    // Get all students from API
    suspend fun getAllStudents(): Result<List<StudentResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStudents()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Failed to fetch students"))
                    }
                } else {
                    Result.failure(Exception("Server error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    // Add student via API
    suspend fun addStudent(student: StudentRequest): Result<StudentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addStudent(student)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Failed to add student"))
                    }
                } else {
                    Result.failure(Exception("Server error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    // Update student via API
    suspend fun updateStudent(student: StudentRequest): Result<StudentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateStudent(student)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Failed to update student"))
                    }
                } else {
                    Result.failure(Exception("Server error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    // Delete student via API
    suspend fun deleteStudent(studentId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteStudent(studentId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(body?.message ?: "Failed to delete student"))
                    }
                } else {
                    Result.failure(Exception("Server error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
}