package com.example.mapproject.model.devices

data class DataX(
    val altitude: Any?,
    val attributes: Attributes?,
    val connected_at: String?,
    val connection_status: String?,
    val course: Int?,
    val fixed_at: String?,
    val location: Location,
    val speed: Int?
)