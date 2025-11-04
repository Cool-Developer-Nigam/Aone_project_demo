package com.nigdroid.aone_project.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nigdroid.aone_project.data.model.Student


@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY timestamp DESC")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): LiveData<Int>
}