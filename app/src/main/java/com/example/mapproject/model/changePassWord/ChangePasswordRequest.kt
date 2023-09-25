package com.example.mapproject.model.changePassWord

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("old_password")
    val oldPass: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passConfirm: String
)
