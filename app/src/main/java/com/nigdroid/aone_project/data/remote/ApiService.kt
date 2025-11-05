package com.nigdroid.aone_project.data.remote

import com.nigdroid.aone_project.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Login endpoint - Returns LoginResponse directly, not wrapped in ApiResponse
    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // Get all students
    @GET("students.php")
    suspend fun getStudents(): Response<ApiResponse<List<StudentResponse>>>

    // Add student
    @POST("students.php")
    suspend fun addStudent(
        @Body student: StudentRequest
    ): Response<ApiResponse<StudentResponse>>

    // Update student
    @PUT("students.php")
    suspend fun updateStudent(
        @Body student: StudentRequest
    ): Response<ApiResponse<StudentResponse>>

    // Delete student
    @DELETE("students.php")
    suspend fun deleteStudent(
        @Query("id") studentId: Int
    ): Response<ApiResponse<Unit>>
}