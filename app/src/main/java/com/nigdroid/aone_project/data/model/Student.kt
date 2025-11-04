package com.nigdroid.aone_project.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val className: String,
    val rollNo: String,
    val contact: String,
    val email: String = "",
    val timestamp: Long = System.currentTimeMillis()
)