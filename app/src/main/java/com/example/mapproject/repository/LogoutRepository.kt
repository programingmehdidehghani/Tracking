package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changeEmail.EmailRequest
import javax.inject.Inject

class LogoutRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun logoutUser(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String) =
        apiService.logoutUser(token,id,language, tz, tu ,du,cu)

}