package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultXX(
    val alerts: MutableList<String>,
    val audits: Audits,
    val configuration_sms: Double,
    val device: Device,
    val geofence: Geofence,
    val languages: MutableList<String>,
    val maps: MutableList<String>,
    val messages: MutableList<String>,
    val pagination: Pagination,
    val permissions: MutableList<String>,
    val roles: MutableList<String>,
    val service: Service,
    val share_location_sms: Double,
    val timezones: MutableList<String>,
    val transaction: Transaction,
    val units: UnitsX
): Parcelable