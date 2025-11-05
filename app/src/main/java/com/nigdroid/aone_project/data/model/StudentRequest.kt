package com.nigdroid.aone_project.data.model

import com.google.gson.annotations.SerializedName

data class StudentRequest(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("class")
    val `class`: String,

    @SerializedName("roll_no")
    val roll_no: String,

    @SerializedName("contact")
    val contact: String
)