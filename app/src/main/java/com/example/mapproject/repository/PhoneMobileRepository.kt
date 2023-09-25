package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changePhoneMobile.ConfirmPhoneMobileRequest
import com.example.mapproject.model.changePhoneMobile.PhoneMobileRequest
import javax.inject.Inject

class PhoneMobileRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun phoneMobileRequest(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,phoneMobileRequest: PhoneMobileRequest)
    = apiService.getCodePhoneNumber(token,id,language, tz, tu ,du,cu,phoneMobileRequest)

    suspend fun confirmPhoneMobile(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,confirmPhoneMobileRequest: ConfirmPhoneMobileRequest)
    = apiService.confirmPhoneNumber(token,id,language, tz, tu ,du,cu,confirmPhoneMobileRequest)
}