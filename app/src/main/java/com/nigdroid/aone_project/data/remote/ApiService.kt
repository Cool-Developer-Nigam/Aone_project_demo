package com.nigdroid.aone_project.data.remote

import com.nigdroid.aone_project.data.model.ApiResponse
import com.nigdroid.aone_project.data.model.LoginRequest
import com.nigdroid.aone_project.data.model.LoginResponse
import com.nigdroid.aone_project.data.model.StudentRequest
import com.nigdroid.aone_project.data.model.StudentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    // Railway PHP API endpoints (WITH .php extension)
    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("students.php")
    suspend fun getStudents(): Response<ApiResponse<List<StudentResponse>>>

    @POST("students.php")
    suspend fun addStudent(
        @Body student: StudentRequest
    ): Response<ApiResponse<StudentResponse>>

    @PUT("students.php")
    suspend fun updateStudent(
        @Body student: StudentRequest
    ): Response<ApiResponse<StudentResponse>>

    @DELETE("students.php")
    suspend fun deleteStudent(
        @Query("id") studentId: Int
    ): Response<ApiResponse<Unit>>
}