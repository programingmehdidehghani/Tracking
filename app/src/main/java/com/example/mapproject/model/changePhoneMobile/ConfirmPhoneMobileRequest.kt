package com.example.mapproject.model.changePhoneMobile

import com.google.gson.annotations.SerializedName

data class ConfirmPhoneMobileRequest(
    @SerializedName("code")
    val code: String,
    @SerializedName("mobile")
    val mobile: String
)
