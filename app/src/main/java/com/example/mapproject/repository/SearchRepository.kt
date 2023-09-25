package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun searchGroup(token: String,id: String,query: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String)
            = apiService.searchGroup(token,id,query,position,deviceId,language,tz,tu,du,cu)
}