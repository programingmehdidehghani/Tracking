package com.example.mapproject.model.changePhoneMobile

import com.google.gson.annotations.SerializedName

data class  PhoneMobileRequest(
    @SerializedName("mobile")
    val mobile: String
)
