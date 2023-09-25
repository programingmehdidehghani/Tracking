package com.example.mapproject.model.login

import com.google.gson.annotations.SerializedName
data class LoginRequest(
    @SerializedName("username")
    val userName: String,
    @SerializedName("password")
    val pass: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("fcm_token")
    val fcmToken: String
)
