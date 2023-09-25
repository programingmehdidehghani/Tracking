package com.example.mapproject.model.alerts

data class Data(
    val created_at: String?,
    val `data`: Any?,
    val device: Device?,
    val expiration: String?,
    val id: Int?,
    val location: Location?,
    val read_at: Any?,
    val type: String?,
    val updated_at: String?
)