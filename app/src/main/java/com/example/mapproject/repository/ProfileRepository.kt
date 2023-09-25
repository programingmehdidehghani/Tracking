package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProfile(id: String,token: String,language: String, tz: String, tu: String ,du: String, cu: String)
    = apiService.getProfile(token,id,language, tz, tu ,du, cu)

}