package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import javax.inject.Inject

class GetFencesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getFences(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String)
            = apiService.getFences(token,id,language,tz,tu,du,cu)
    suspend fun deleteFences(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String)
            = apiService.deleteFences(token,id,language,tz,tu,du,cu)
}