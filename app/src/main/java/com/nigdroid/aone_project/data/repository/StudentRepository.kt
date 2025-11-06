package com.nigdroid.aone_project.data.repository

import android.util.Log
import com.google.gson.Gson
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

    companion object {
        private const val TAG = "StudentRepository"
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "=== LOGIN ATTEMPT ===")
                Log.d(TAG, "Email: $email")
                Log.d(TAG, "Password length: ${password.length}")

                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response success: ${response.isSuccessful}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Log.e(TAG, "Response body is NULL!")
                        return@withContext Result.failure(Exception("Empty response from server"))
                    }

                    Log.d(TAG, "Response body: $body")
                    Log.d(TAG, "Success: ${body.success}")
                    Log.d(TAG, "Message: ${body.message}")
                    Log.d(TAG, "Token: ${body.token?.take(10)}...")
                    Log.d(TAG, "Data: ${body.data}")

                    if (body.success && body.token != null && body.data != null) {
                        Log.d(TAG, "✓ Login successful!")
                        Result.success(body)
                    } else {
                        val errorMsg = body.message
                        Log.e(TAG, "✗ Login failed: $errorMsg")
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }

                    Log.e(TAG, "Server error response:")
                    Log.e(TAG, "Code: ${response.code()}")
                    Log.e(TAG, "Error body: $errorBody")

                    val errorMsg = when (response.code()) {
                        400 -> "Invalid request. Check your credentials."
                        401 -> "Invalid email or password"
                        404 -> "Login service not found. Check server URL."
                        500 -> "Server error. Try again later."
                        else -> "Server error (${response.code()})"
                    }

                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "=== LOGIN EXCEPTION ===")
                Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Exception message: ${e.message}")
                e.printStackTrace()

                val errorMsg = when {
                    e is com.google.gson.JsonSyntaxException -> {
                        "Server returned invalid data. Response was not valid JSON."
                    }
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> {
                        "Cannot connect to server. Check internet connection."
                    }
                    e.message?.contains("timeout", ignoreCase = true) == true -> {
                        "Connection timeout. Try again."
                    }
                    e.message?.contains("Failed to connect", ignoreCase = true) == true -> {
                        "Cannot reach server. Check URL and internet."
                    }
                    else -> {
                        "Network error: ${e.message}"
                    }
                }

                Result.failure(Exception(errorMsg))
            }
        }
    }

    suspend fun getAllStudents(): Result<List<StudentResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching all students")

                val response = apiService.getStudents()

                Log.d(TAG, "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Log.d(TAG, "Students fetched: ${body.data.size}")
                        Result.success(body.data)
                    } else {
                        val errorMsg = body?.message ?: "Failed to fetch students"
                        Log.e(TAG, errorMsg)
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorMsg = "Server error: ${response.code()}"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get students exception", e)
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun addStudent(student: StudentRequest): Result<StudentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding student: ${student.name}")

                val response = apiService.addStudent(student)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Log.d(TAG, "Student added successfully")
                        Result.success(body.data)
                    } else {
                        val errorMsg = body?.message ?: "Failed to add student"
                        Log.e(TAG, errorMsg)
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorMsg = "Server error: ${response.code()}"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Add student exception", e)
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun updateStudent(student: StudentRequest): Result<StudentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Updating student ID: ${student.id}")

                val response = apiService.updateStudent(student)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Log.d(TAG, "Student updated successfully")
                        Result.success(body.data)
                    } else {
                        val errorMsg = body?.message ?: "Failed to update student"
                        Log.e(TAG, errorMsg)
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorMsg = "Server error: ${response.code()}"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Update student exception", e)
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun deleteStudent(studentId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Deleting student ID: $studentId")

                val response = apiService.deleteStudent(studentId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Log.d(TAG, "Student deleted successfully")
                        Result.success(Unit)
                    } else {
                        val errorMsg = body?.message ?: "Failed to delete student"
                        Log.e(TAG, errorMsg)
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorMsg = "Server error: ${response.code()}"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Delete student exception", e)
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
}