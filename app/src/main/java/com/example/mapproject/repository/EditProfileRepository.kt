package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.changeInformationProfile.ChangeProfileInformationRequest
import okhttp3.MultipartBody
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun editProfile(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,changeProfileInformationRequest: ChangeProfileInformationRequest)
            = apiService.editProfile(token,id,language, tz, tu ,du,cu,changeProfileInformationRequest)

    suspend fun updatePictureProfile(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,pictureProfile: MultipartBody.Part)
            = apiService.updatePictureProfile(token,id,language, tz, tu ,du,cu,pictureProfile)
}