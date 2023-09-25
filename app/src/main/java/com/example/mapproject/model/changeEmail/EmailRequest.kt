package com.example.mapproject.model.changeEmail

import com.google.gson.annotations.SerializedName

data class EmailRequest(
    @SerializedName("email")
    val email: String
)
