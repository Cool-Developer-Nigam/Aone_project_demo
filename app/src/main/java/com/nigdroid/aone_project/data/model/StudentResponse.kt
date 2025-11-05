package com.nigdroid.aone_project.data.model

import com.google.gson.annotations.SerializedName

data class StudentResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("class")
    val className: String,

    @SerializedName("roll_no")
    val rollNo: String,

    @SerializedName("contact")
    val contact: String
)