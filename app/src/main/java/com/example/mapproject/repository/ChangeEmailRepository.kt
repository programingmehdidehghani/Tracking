package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changeEmail.EmailRequest
import com.example.mapproject.model.changeEmail.VerifyCodeEmailRequest
import javax.inject.Inject

class ChangeEmailRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun changeEmail(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, emailRequest: EmailRequest)
    = apiService.changeEmail(token,id,language, tz, tu ,du,cu,emailRequest)

    suspend fun verifyEmail(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,verifyCodeEmailRequest: VerifyCodeEmailRequest)
            = apiService.verifyCodeEmail(token,id,language, tz, tu ,du,cu,verifyCodeEmailRequest)
}