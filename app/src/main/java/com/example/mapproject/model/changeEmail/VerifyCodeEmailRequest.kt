package com.example.mapproject.model.changeEmail

import com.google.gson.annotations.SerializedName

data class VerifyCodeEmailRequest(
    @SerializedName("code")
    val code: String,
    @SerializedName("email")
    val email: String
)
