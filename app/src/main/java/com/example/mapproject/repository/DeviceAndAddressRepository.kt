package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import javax.inject.Inject

class DeviceAndAddressRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDevices(token: String,id: String,position: String, deviceId: String,language: String, tz: String, tu: String ,du: String, cu: String)
    = apiService.getDevices(token,id,position,deviceId,language,tz,tu,du,cu)

    suspend fun getAddressLocation(token: String,latitude: String,longitude: String,language: String, tz: String, tu: String ,du: String, cu: String)
            = apiService.getAddressLocation(token,latitude,longitude,language,tz,tu,du,cu)
}