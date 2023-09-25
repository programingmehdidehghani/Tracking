package com.example.mapproject.model.forgetPass

import com.google.gson.annotations.SerializedName

data class RequestForgetPass(
    @SerializedName("username")
    val username: String,
)
