package com.example.mapproject.repository

import com.example.mapproject.api.ApiService
import com.example.mapproject.model.circleDrawMap.DrawCircleMap
import com.example.mapproject.model.drawPolygon.DrawPolygon
import com.example.mapproject.model.editFences.EditFences
import javax.inject.Inject

class DrawPolygonRepository @Inject constructor(
    private val apiService: ApiService
){
    suspend fun drawPolygon(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawPolygon: DrawPolygon) =
        apiService.drawPolyGon(token,id,language,tz,tu,du,cu,drawPolygon)

    suspend fun drawCircle(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,drawCircleMap: DrawCircleMap) =
        apiService.drawCircleMap(token,id,language,tz,tu,du,cu,drawCircleMap)

    suspend fun editFences(token: String,id: String,language: String, tz: String, tu: String ,du: String, cu: String,editFences: EditFences) =
        apiService.editFences(token,id,language,tz,tu,du,cu,editFences)
}