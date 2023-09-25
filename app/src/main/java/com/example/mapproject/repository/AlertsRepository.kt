package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import javax.inject.Inject

class AlertsRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAlerts(token: String,
                          id: String,
                          from: String,
                          status: String,
                          to: String,
                          type: String,
                          orderBy: String,
                          orderType: String,
                          page: String,
                          perPage: String,
                          deviceId: String,
                          language: String,
                          tz: String,
                          tu: String,
                          du: String,
                          cu: String)
    = apiService.getAlerts(token,
        id,
        from,
        status,
        to,
        type,
        orderBy,
        orderType,
        page,
        perPage,
        deviceId,
        language,
        tz,
        tu,
        du,
        cu)
}