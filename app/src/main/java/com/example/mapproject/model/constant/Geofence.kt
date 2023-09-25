package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Geofence(
    val types: List<String>
): Parcelable