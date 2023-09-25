package com.example.mapproject.model.verifyForgetPass

import com.google.gson.annotations.SerializedName

data class VerifyForgetPass(
    @SerializedName("code")
    val code: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String,
    @SerializedName("username")
    val username: String,
)
