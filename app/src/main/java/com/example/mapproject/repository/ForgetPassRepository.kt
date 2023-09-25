package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.forgetPass.RequestForgetPass
import com.example.mapproject.model.verifyForgetPass.VerifyForgetPass
import javax.inject.Inject

class ForgetPassRepository @Inject constructor(
    private val apiService: ApiService
){

    suspend fun requestForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,forgetPass: RequestForgetPass) =
        apiService.requestForgetPass(language, tz, tu ,du, cu,forgetPass)

    suspend fun requestVerifyForgetPass(language: String, tz: String, tu: String ,du: String, cu: String,verifyForgetPass: VerifyForgetPass) =
        apiService.verifyForgetPass(language, tz, tu ,du, cu,verifyForgetPass)
}