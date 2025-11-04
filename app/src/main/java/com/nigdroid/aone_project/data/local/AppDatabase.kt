package com.nigdroid.aone_project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nigdroid.aone_project.data.model.Student

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}