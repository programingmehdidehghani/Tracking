package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.login.LoginRequest
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getConstant(language: String, tz: String, tu: String ,du: String, cu: String) =
        apiService.getConstant(language, tz, tu ,du, cu)

    suspend fun loginRequest(language: String, tz: String, tu: String ,du: String, cu: String ,loginRequest: LoginRequest)
    = apiService.login(language, tz, tu ,du, cu,loginRequest)


}