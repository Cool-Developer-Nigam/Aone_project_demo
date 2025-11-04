package com.nigdroid.aone_project.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nigdroid.aone_project.data.repository.StudentRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    repository: StudentRepository
) : ViewModel() {

    val studentCount: LiveData<Int> = repository.studentCount
}