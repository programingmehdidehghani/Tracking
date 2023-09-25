package com.example.mapproject.model.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Device(
    val icons: MutableList<String>,
    val types: MutableList<String>
): Parcelable