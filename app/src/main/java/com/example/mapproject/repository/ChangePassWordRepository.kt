package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changePassWord.ChangePasswordRequest
import javax.inject.Inject

class ChangePassWordRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getResultChangePass(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,changePassword: ChangePasswordRequest)
    = apiService.changePassword(token,id,language, tz, tu ,du,cu,changePassword)
}