package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.SendNameToServer
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun addGroup(token: String, id: String, language: String, tz: String, tu: String, du: String, cu: String, sendNameToServer: SendNameToServer)
            = apiService.addGroups(token,id,language,tz,tu,du,cu,sendNameToServer)

    suspend fun deleteGroup(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String)
            = apiService.deleteGroup(token,id,language,tz,tu,du,cu)

    suspend fun editGroup(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,sendNameToServer: SendNameToServer)
            = apiService.editGroup(token,id,language,tz,tu,du,cu,sendNameToServer)
}