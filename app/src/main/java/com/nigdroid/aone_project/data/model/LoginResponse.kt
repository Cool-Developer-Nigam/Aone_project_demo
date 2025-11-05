package com.nigdroid.aone_project.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("data")
    val data: UserData? = null
)

data class UserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String
)